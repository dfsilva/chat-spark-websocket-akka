var app = new Vue({
	el : '#app',
	data : {
		usuario : {
			autenticado : false,
			email : ""
		},
		message : '',
		chats : {},
		selectedChat : '',
		usuariosConectados: [],
		timeoutconexao: null,
        timetoutMessage: null
	},
	methods : {
		exibirMensagen : function(mensagem) {
            app.message = mensagem;
			console.log(app.timetoutMessage);

			if (app.timetoutMessage) {
                window.clearTimeout(app.timetoutMessage);
                app.timetoutMessage = null;
			}

            app.timetoutMessage = window.setTimeout(function() {
                app.message = '';
                app.timetoutMessage = null;
			}, 3000)
		},

		conectar: function(){
            window.WebSocket = window.WebSocket || window.MozWebSocket;
            app.connection = new WebSocket('ws://127.0.0.1:8080/chat');
            app.connection.onopen = onOpen;
            app.connection.onclose = onClose;
            app.connection.onmessage = onMessage;
		},

		login : function() {
			console.log("Entrando com o email: " + this.usuario.email)
			if (!this.usuario.email) {
				this.exibirMensagen("Informe o email");
			} else {
				var messageStr = JSON.stringify({
					acao : "login",
					data : {
						email : this.usuario.email
					}
				});
				console.log(messageStr);
				this.connection.send(messageStr);
			}
		},

		iniciarConversa: function(usuario){
			console.log("Iniciando conversa: " + usuario);
			app.chats[usuario] = {
				carregado: false,
				mensagens:[],
				texto:""
			};
			app.selectedChat = usuario;
		},
		
		enviarMensagem: function (chat) {
			console.log("enviando texto: " + app.chats[chat].texto);

            var messageStr = JSON.stringify({
                acao : "enviar-mensagem",
                usuario: app.usuario.email,
                data : {
                    para : chat,
                    texto: app.chats[chat].texto
                }
            });

            console.log("enviando " + messageStr);
            app.chats[chat].texto = "";
            this.connection.send(messageStr);
        }
	},
	created : function() {
		setTimeout(function () {
            app.conectar();
        }, 2000)
	}
});

function onMessage(message){
    var response = JSON.parse(message.data);
    if (response.acao === "login_response") {
        console.log(response.data.sucesso);
        Vue.set(app.usuario, 'autenticado', response.data.sucesso)
    }else if (response.acao === "novo_usuario") {
        console.log("recebeu usuario conectado: "+response.data);
        app.usuariosConectados.push(response.data.email);
    }else if(response.acao === "usuario_desconectado"){
        console.log('usuario desconectado: '+ response.data.email);
        var novosUsuarios = app.usuariosConectados.filter(function(value){
            return value !== response.data.email;
        })
        console.log('usuarios apos remocao: '+ novosUsuarios);
        Vue.set(app, 'usuariosConectados', novosUsuarios)
    }else if(response.acao === "atualizar_mensagens"){
        console.log('atualizando mensagens: '+ response.data.chat);
        app.chats[response.data.chat].mensagens = response.data.mensagens;
	}
}

function onClose(){
    app.exibirMensagen("conexao fechada")
    app.timeoutconexao = window.setTimeout(function(){
        app.conectar();
    }, 3000);
}

function onOpen(){
    app.exibirMensagen("conexao aberta")
    if(app.timeoutconexao){
        window.clearTimeout(app.timeoutconexao);
        app.timeoutconexao = null;
    }
}