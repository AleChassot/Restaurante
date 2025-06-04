# Sistema de Restaurante

Sistema de gerenciamento para restaurantes desenvolvido em Spring Boot, permitindo controle de pedidos, mesas, cardápio e pagamentos.

## Requisitos

- Java 17 ou superior
- Maven
- MySQL 8.0 ou superior

## Configuração do Banco de Dados

1. Crie um banco de dados MySQL chamado `restaurante_db`
2. Configure as credenciais do banco de dados no arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/restaurante_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

## Configuração do Projeto

1. Clone o repositório:
```bash
git clone [URL_DO_REPOSITÓRIO]
cd Trabalho_Final_Restaurante
```

2. Compile o projeto:
```bash
mvn clean install
```

## Executando o Projeto

1. Execute o projeto usando Maven:
```bash
mvn spring-boot:run
```

2. Acesse a aplicação no navegador:
```
http://localhost:8080
```

## Funcionalidades Principais

### Gestão de Pedidos
- Criação de novos pedidos
- Edição de pedidos existentes
- Exclusão de pedidos
- Visualização de pedidos pendentes
- Filtro de pedidos por cliente e data

### Gestão de Mesas
- Controle de ocupação de mesas
- Liberação automática após pagamento

### Gestão de Cardápio
- Cadastro de itens
- Categorização de produtos
- Controle de preços

### Gestão de Pagamentos
- Registro de pagamentos
- Suporte a múltiplas formas de pagamento
- Cálculo automático de taxa de serviço
- Controle de pagamentos parciais

## Estrutura do Projeto

```
src/main/java/com/example/SpringAula2/
├── controller/     # Controladores da aplicação
├── model/         # Entidades do sistema
├── repository/    # Repositórios de dados
├── service/       # Lógica de negócios
└── exception/     # Tratamento de exceções
```

## Tecnologias Utilizadas

- Spring Boot
- Spring Data JPA
- Thymeleaf
- Bootstrap
- MySQL
- Maven

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Suporte

Em caso de dúvidas ou problemas, abra uma issue no repositório do projeto. 