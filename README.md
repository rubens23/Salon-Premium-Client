<h1 align="center">Salon Premium Client</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=24"><img src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat" border="0" alt="API"></a>
  <br>
  <a href="https://wa.me/+5511961422254"><img alt="WhatsApp" src="https://img.shields.io/badge/WhatsApp-25D366?style=for-the-badge&logo=whatsapp&logoColor=white"/></a>
  <a href="https://www.linkedin.com/in/rubens-francisco-125529162/"><img alt="Linkedin" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"/></a>
  <a href="mailto:rubens_assis@outlook.com.br"><img alt="Outlook" src="https://img.shields.io/badge/Microsoft_Outlook-0078D4?style=for-the-badge&logo=microsoft-outlook&logoColor=white"/></a>
</p>

<p align="center">  

⭐ Esse é um projeto para demonstrar minha capacidade no desenvolvimento Android nativo com Kotlin. Mais informações técnicas abaixo.

✂️ Salon Premium Client é um aplicativo para android que possibilita que clientes de um salão vejam os serviços, funcionários e horários disponíveis e marquem atendimentos em um salão de beleza.

</p>

</br>

<p float="left" align="center">


<img alt="screenshot" width="30%" src="app/src/main/appscreenshots/1-portrait.png"/>
<img alt="screenshot" width="30%" src="app/src/main/appscreenshots/6-portrait.png"/>
<img alt="screenshot" width="30%" src="app/src/main/appscreenshots/3-portrait.png"/>
<img alt="screenshot" width="30%" src="app/src/main/appscreenshots/4-portrait.png"/>
<img alt="screenshot" width="30%" src="app/src/main/appscreenshots/5-portrait.png"/>

</p>

## Download do App

Faça o download do <a href="apk/app-debug.apk?raw=true">APK diretamente</a>. Você pode ver <a href="https://www.google.com/search?q=como+instalar+um+apk+no+android">aqui</a> como instalar um APK no seu aparelho android!

## Tecnologias usadas

- Minimum SDK level 24
- [Linguagem Kotlin](https://kotlinlang.org/)

- Componentes da SDK do android que foram utilizados:
  - Navigation: Utilizado para facilitar a navegação entre os fragments do meu app. Fiz a integração com a bottom navigation view para conseguir gerenciar facilmente a seleção dos destinos e back stack.
  - viewModel: Faz parte do MVVM que usei para organizar meu projeto. Minhas viewModels servem como acesso aos meus repositories de acesso a data source(que nesse caso são algumas apis do firebase) e essa classe tambem tem como membros as variaveis que se atualizadas devem desencadear alguma mudança na tela.
  - fragment: nesse projeto utilizei fragments para organizar a lógica das minhas diferentes telas.
  - SharedFlow: Esse componente vem das bibliotecas do kotlin e me ajuda a fornecer um componente que avisa à tela que precisa ser atualizada. Escolhi o sharedFlow pois, à princípio, não queria guardar os valores já emitidos em cache. Mas talvez eu mude isso no futuro.
  - viewBinding: Essa biblioteca me ajuda muito quando eu utilizo xmls para fazer minhas telas. Eu consigo acessar facilmente os componentes dos layouts sem ter que escrever muito.

- Outras dependências:
  - CircleImageView: utilizei essa biblioteca do github para usar imageviews arredondadas e bordas para representar seleções. É uma biblioteca que já utilizo há algum tempo porque funciona muito bem quando estou usando xmls.
  - MonthSelector: utilizei essa biblioteca que eu mesmo criei e que serve para fornecer uma view para selecionar meses do ano.
  - DaggerHilt: Biblioteca incrível que facilita a injeção de dependências nas minhas classes. Você pode dividir tudo por modulos e deixar tudo bem separadinho, para cada objeto ir para onde precisa ir e as vezes sem criar instancias repetidas.
  - Glide: usei o glide também. Essa biblioteca é principalmente útil quando você quer consumir uma imagem que vem de uma url e já fazer transformações na imagem.
  - FirebaseAuth: Nesse projeto utilizei o firebaseAuth para implementar o cadastro e login dos usuários.
  - FirebaseDatabase: Utilizei o banco de dados do firebase para salvar coisas que precisavam ser persistidas como agendamentos, dados de perfil dos clientes e etc.
  - FirebaseStorage: usei para salvar arquivos de foto como a foto de perfil dos usuários.

