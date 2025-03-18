## Projeto Meetime - Roberta Carvalho

### Tecnologias Usadas
* Spring Boot (Java 17)
* Maven para build e gerenciamento de dependências
* Ngrok para expor a aplicação à internet
* Docker para criação de contêineres

### Pré-requisitos
Antes de começar, certifique-se de ter os seguintes requisitos instalados e que todos estejam configurados no PATH nas variáveis do sistema:

Instalação do Docker: https://www.docker.com/get-started   
Instalação do Docker Compose: https://docs.docker.com/compose/install/  
Instalação do ngrok: https://ngrok.com/downloads/windows?tab=download

Deve-se ter também:
1. Uma conta de desenvolvedor pública gratuita
2. Uma conta HubSpot para instalar o aplicativo – Pode ser uma conta já existente ou uma conta de teste, mas deve ter permissões de super-admin
3. Um aplicativo vinculado à sua conta de desenvolvedor no HubSpot
    * No seu aplicativo na aba "Autenticação", configure a URL de redirecionamento como "localhost:8080/auth/redireciona" e adicione os seguintes escopos necessários "crm.objects.contacts.read" e "crm.objects.contacts.write"

### Como executar o projeto?
1. Expor a aplicação com Ngrok
    * Abra um prompt de comando e execute o comando **ngrok authtoken 2uQIou68ceMW2tRHgdDlI6pm0q2_4hbwLdsoZV6NhXCPav7rx** (criei uma conta de teste no ngrok para gerar essa chave)
    * Execute o comando **ngrok http 8080**
    * Copie a URL gerada (que está no campo Forwarding) e configure-a como "URL de destino" no webhook do seu aplicativo no HubSpot
    * Crie a assinatura para contact.creation no HubSpot
    * Ative a assinatura em Assinaturas de eventos > Ativar

2. Clone o repositório  
   Abra outro prompt de comando (diferente do que está rodando o Ngrok) e execute os comandos:  
   **git clone https://github.com/robertaassis/hubspot.git**  
   **cd hubspot**

3. Substituição de varíaveis  
   No arquivo src/main/resources/application.properties, substitua os seguintes valores pelas suas credenciais do HubSpot (disponível na página de configurações do aplicativo):  
   spring.security.oauth2.client.registration.hubspot.client-id=SEU_CLIENT_ID  
   spring.security.oauth2.client.registration.hubspot.client-secret=SEU_CLIENT_SECRET

4. Construa a imagem e suba o contêiner  
   i. Na raiz do repositório, execute o comando a seguir para construir a imagem e subir o contêiner: **docker-compose up --build**  
   Obs.: Esse comando irá executar os seguintes passos:
    * Construir a imagem do projeto
    * Baixar as dependências do Maven
    * Rodar a aplicação Spring Boot
    * Expor a aplicação na porta 8080

5. Acesso à aplicação  
   Após os passos acima, a aplicação estará disponível em http://localhost:8080 para testes.

6. Baixe e importe o arquivo "Meetime - Roberta Carvalho.postman_collection.json" no Postman para testar os endpoints disponíveis.
