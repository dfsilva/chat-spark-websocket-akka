var app = new Vue({
	el : '#app',
	data : {
		usuario : {
			autenticado : false,
			email : ""
		},
		message : '',
		chats : {},
		selectedChat : ''
	},
	methods : {
		exibirMensagen : function(mensagem) {
			this.message = mensagem;

			if (this.timetoutMessage) {
				this.timetoutMessage.cancel();
			}

			this.timetoutMessage = window.setTimeout(function() {
				app.message = '';
				this.timetoutMessage = null;
			}, 3000)
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

		conectar : function() {
			window.WebSocket = window.WebSocket || window.MozWebSocket;
			this.connection = new WebSocket('ws://127.0.0.1:4567/chat');

			this.connection.onopen = function() {
				app.exibirMensagen("conexao aberta")
			};

			this.connection.onerror = function(error) {
				app.exibirMensagen("conexao fechada")
			};

			this.connection.onmessage = function(message) {

				var response = JSON.parse(message.data);
				if (response.acao === "login_response") {
					console.log(response.data.sucesso);
					Vue.set(app.usuario, 'autenticado', response.data.sucesso)
				}

				/*
				 * app.chats[app.selectedChat].push({ message : message.data })
				 */
			};
		}
	},
	created : function() {
		this.conectar();
	}
});