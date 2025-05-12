package com.senac.torreverde.ms_compra.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class PedidoDTO implements Serializable {

    private Integer pedidoId;
    private Integer usuarioId;
    private BigDecimal pedidoValorTotal;
    private List<PedidoItemDTO> itens;

    public PedidoDTO() {
    }

    public PedidoDTO(Integer pedidoId, Integer usuarioId, BigDecimal pedidoValorTotal, List<PedidoItemDTO> itens) {
        this.pedidoId = pedidoId;
        this.usuarioId = usuarioId;
        this.pedidoValorTotal = pedidoValorTotal;
        this.itens = itens;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getPedidoValorTotal() {
        return pedidoValorTotal;
    }

    public void setPedidoValorTotal(BigDecimal pedidoValorTotal) {
        this.pedidoValorTotal = pedidoValorTotal;
    }

    public List<PedidoItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItemDTO> itens) {
        this.itens = itens;
    }
}
