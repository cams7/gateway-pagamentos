function openShopline(dadosCriptografados) {
	console.log(dadosCriptografados);
	
	var formShopline = $("div#shopline").find("form");
	formShopline.find("input[name='DC']").val(dadosCriptografados);
	formShopline.submit();
}

function carregabrw() {
	window	.open("", "SHOPLINE", "toolbar = yes,menubar=yes,resizable=yes,status=no,sc rollbars=yes,width=815,height=575");
}