const criaAjaxGet = function(url, dados, func) {
    let ajax = new XMLHttpRequest();
    ajax.onreadystatechange = func;
    ajax.open("GET", url+"?"+dados, true);
    ajax.send();
};

const criaAjaxPost = function(url, dados, func) {
    let ajax = new XMLHttpRequest();
    ajax.onreadystatechange = func;
    ajax.open("POST", url, true);
    ajax.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    ajax.send(dados);
};

const mostrar = function () {
    this.onload = ()=> {
        let pessoas = this.responseXML.documentElement.getElementsByTagName("pessoa");
        let txt = "<table border=1><tr><th>Código</th><th>Nome</th><th>Idade</th>";
        for (let pessoa of pessoas) {
            let codigo = pessoa.getElementsByTagName("codigo")[0].firstChild.nodeValue;
            let nome = pessoa.getElementsByTagName("nome")[0].firstChild.nodeValue;
            let idade = pessoa.getElementsByTagName("idade")[0].firstChild.nodeValue;
            txt += `<tr><td>${codigo}</td><td>${nome}</td><td>${idade}</td></tr>`;
        }
        txt += "</table>";
        document.getElementById("lista").innerHTML = txt;
        nome = document.getElementById("nome").value="";
        idade = document.getElementById("idade").value="";
    };
};

const inserirDados = (e) => {
    e.preventDefault();
    let nome = document.getElementById("nome").value;
    let idade = document.getElementById("idade").value;
    let dados = `nome=${nome}&idade=${idade}`;
    criaAjaxPost("inserir", dados,
        function () {
            this.onload = ()=> {
                alert(this.responseText);
                criaAjaxGet("listar","",mostrar);
            };
        }
    );
};

document.getElementById("botao").onclick = inserirDados;
onload = ()=> {criaAjaxGet("listar","",mostrar);};