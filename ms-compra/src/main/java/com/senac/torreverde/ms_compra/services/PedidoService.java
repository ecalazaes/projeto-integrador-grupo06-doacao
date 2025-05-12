package com.senac.torreverde.ms_compra.services;

import com.senac.torreverde.ms_compra.dtos.PedidoDTO;
import com.senac.torreverde.ms_compra.dtos.PedidoItemDTO;
import com.senac.torreverde.ms_compra.entities.Cupom;
import com.senac.torreverde.ms_compra.entities.Pedido;
import com.senac.torreverde.ms_compra.entities.PedidoItem;
import com.senac.torreverde.ms_compra.repositories.PedidoRepository;
import com.senac.torreverde.ms_compra.services.rabbitmq.PedidoPublisher;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CupomService cupomService;

    @Autowired
    private PedidoItemService pedidoItemService;

    @Autowired
    private PedidoItemDoacaoService pedidoItemDoacaoService;

    @Autowired
    private PedidoPublisher pedidoPublisher;


    // Medodo para buscar todos pedidos
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    // Metodo para buscar um pedido por id
    public Optional<Pedido> findById(Integer id) {
        return pedidoRepository.findById(id);
    }

    // Metodo para buscar um pedido por id do usuario
    public List<Pedido>  findByUsuarioId(Integer id) {
        return pedidoRepository.findByUsuarioId(id);
    }

    // Meodo para criar um pedido
    @Transactional
    public Pedido save(Pedido pedido) {

        // 1. Validações básicas
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item");
        }

        // 2. Carrega o cupom COMPLETO do banco se existir
        Cupom cupomValido = cupomService.validarCupom(pedido.getCupom());
        pedido.setCupom(cupomValido);

        // 3. Configura dados iniciais do pedido
        pedido.setData(LocalDateTime.now());
        pedido.setStatus(0);

        // 4. Estabelece a relação bidirecional ANTES de salvar
        pedido.getItens().forEach(item -> pedidoItemService.vincularPedidoItemAoPedido(item, pedido));

        // 5. Calcula valor total ANTES de salvar
        calcularValorTotal(pedido);

        // 6. Salva o Pedido (cascade cuida dos itens)
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 7. Salva os PedidoItemDoacao, se existirem, após o PedidoItem estar salvo
        pedido.getItens().forEach(item -> pedidoItemDoacaoService.salvarDoacao(item.getDoacao()));

        // 8. Converte os itens do pedido para DTOs para envio via RabbitMQ
        List<PedidoItemDTO> itensDTO = pedido.getItens().stream()
                .map(item -> {
                    PedidoItemDTO dto = new PedidoItemDTO();
                    dto.setEstoqueId(item.getEstoqueId());
                    dto.setQuantidade(item.getQuantidade());
                    dto.setPrecoUnitario(item.getPrecoUnitario());
                    dto.setSubTotal(item.getSubTotal());
                    return dto;
                })
                .toList();

        // 9. Converte o Pedido salvo para PedidoDTO para enviar mensagem ao rabbitmq
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setPedidoId(pedidoSalvo.getId());
        pedidoDTO.setUsuarioId(pedidoSalvo.getUsuarioId());
        pedidoDTO.setPedidoValorTotal(pedidoSalvo.getValorTotal());
        pedidoDTO.setItens(itensDTO);

        pedidoPublisher.enviarPedidoCriado(pedidoDTO);

        return pedidoSalvo;
    }

    // Metodo para deletar um pedido
    public void delete(Integer id) {
        pedidoRepository.deleteById(id);
    }

    // Metodo para calcular o valor total do pedido
    private void calcularValorTotal(Pedido pedido) {
        BigDecimal total = pedido.getItens().stream()
                .map(PedidoItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDoacoes = pedido.getItens().stream()
                .filter(item -> item.getDoacao() != null)
                .map(item -> item.getDoacao().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        total = total.add(totalDoacoes);; // Adiciona o valor das doações ao total do pedido

        if (pedido.getCupom() != null) {
            BigDecimal desconto = calcularDesconto(pedido.getCupom(), total);
            total = total.subtract(desconto);
            pedido.setDescontoAplicado(desconto);
        }

        pedido.setValorTotal(total);
    }

    private BigDecimal calcularDesconto(Cupom cupom, BigDecimal valorTotal) {
        if (cupom.getTipoDesconto() == 1) { // Tipo de desconto: Porcentagem
            BigDecimal percentualDesconto = cupom.getValorDesconto().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return valorTotal.multiply(percentualDesconto).setScale(2, RoundingMode.HALF_UP);
        } else { // Tipo de desconto: Valor fixo
            return valorTotal.min(cupom.getValorDesconto()).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
