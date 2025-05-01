# Microserviço de Compra e Pagamento

Este repositório contém dois microserviços: **ms-compra** e **ms-pagamento**, que funcionam de forma independente e se comunicam via RabbitMQ, com intuito de testar a funcionalidade de doação.


## Configuração do Banco de Dados

As aplicações estão se conectando com os bancos criados pelo Jhonny.
- Servidor Mysql: edumysql.acesso.rj.senac.br
- Porta: 3306
- Usuário: adstv
- Senha: adsads

- Banco: bdtorrevcarrinho
- Banco: bdtorrevpagamento

## Configuração do RabbitMQ

### Subir o RabbitMQ no Docker

Execute o seguinte comando no cmd para iniciar o RabbitMQ via Docker:

```sh
docker run -d --name rabbitmq -p 15672:15672 -p 5672:5672 rabbitmq:management
````

ou 

Execute o seguinte comando no terminal de uma das aplicações ms-compra ou ms-pagamento:

```sh
docker-compose up -d
````

### Acessando o painel de gerenciamento do RabbitMQ

- **URL:** [http://localhost:15672/](http://localhost:15672/)  
- **Usuário:** `guest`  
- **Senha:** `guest`  

## Executando o Microserviço de Compra

### Passos:
1. Rodar o arquivo **MsCompraApplication**.
2. Testar a funcionalidade de doação em um pedido via Swagger:

   - **URL:** [http://localhost:8081/api](http://localhost:8080/api)
  
### Pedido Controller

| Método | Endpoint                | Descrição                               |
|--------|-------------------------|-------------------------------------------|
| `GET`  | `/pedidos/{id}`         | Obtém os detalhes de um pedido específico |
| `PUT`  | `/pedidos/{id}`         | Atualiza os dados de um pedido específico |
| `DELETE`| `/pedidos/{id}`         | Exclui um pedido específico              |
| `GET`  | `/pedidos`              | Lista todos os pedidos                    |
| `POST` | `/pedidos`              | Cria um novo pedido                      |
| `GET`  | `/pedidos/usuario/{usuarioId}` | Lista os pedidos de um usuário específico |

### Cupom Controller

| Método | Endpoint             | Descrição                               |
|--------|----------------------|-------------------------------------------|
| `GET`  | `/cupons/{id}`       | Obtém os detalhes de um cupom específico |
| `PUT`  | `/cupons/{id}`       | Atualiza os dados de um cupom específico |
| `DELETE`| `/cupons/{id}`       | Exclui um cupom específico              |
| `GET`  | `/cupons`            | Lista todos os cupons                     |
| `POST` | `/cupons`            | Cria um novo cupom                       |
| `GET`  | `/cupons/codigo/{codigo}` | Obtém os detalhes de um cupom pelo código |

### Exemplo de requisição JSON:

```json
Exemplo de JSON de pedido sem doação e sem desconto

{
  "usuarioId": 123,  
  "observacoes": "Pedido sem doação e com desconto",
  "itens": [
    {
      "estoqueId": 1,
      "quantidade": 2,
      "precoUnitario": 100.0     
    }
  ]
}


Exemplo de JSON de pedido sem doação e com desconto

{
  "usuarioId": 123,
  "cupom": {
    "id": 2
  },
  "observacoes": "Pedido sem doação e com desconto",
  "itens": [
    {
      "estoqueId": 1,
      "quantidade": 2,
      "precoUnitario": 100.0     
    }
  ]
}


Exemplo de JSON de Pedido com doação e sem cupom de desconto.

{
  "usuarioId": 123,
  "observacoes": "Pedido com doação e sem desconto",
  "itens": [
    {
      "estoqueId": 1,
      "quantidade": 2,
      "precoUnitario": 100.0,
      "doacao": {
        "valor": 20.0
      }
    }
  ]	  
}


Exemplo de JSON Pedido com doação e cupom de desconto.

{
  "usuarioId": 123,
  "cupom": {
    "id": 2
  },
  "observacoes": "Pedido com doação e com desconto",
  "itens": [
    {
      "estoqueId": 1,
      "quantidade": 2,
      "precoUnitario": 100.0,
      "doacao": {
        "valor": 20.0
      }
    }
  ]
}
````

## Executando o Microserviço de Pagamento

### Passos:
1. Rodar o arquivo **MsPagamentoApplication**.
2. Testar a funcionalidade de doação direta no pagamento via Swagger:

   - **URL:** [http://localhost:8081/api](http://localhost:8082/api)
  
### Registro Pagamento Controller

| Método | Endpoint                 | Descrição                      |
|--------|--------------------------|---------------------------------|
| `POST` | `/pagamentos/doacoes`    | Registra um pagamento de doação |

### Exemplo de requisição JSON:

```json
Exemplo de JSON para doação direto no pagamento

{
  "usuarioId": 123,
  "detalhesDoacao": {
    "valorDoacao": 50.00,
    "doadorNome": "João da Silva",
    "doadorIdentificacao": "123.456.789-00",
    "doadorTipo": 1,
    "doadorPaisOrigem": "Brasil",
    "formaTransferencia": "Pix",
    "idTransacao": "E123456789"
  }
}
````
