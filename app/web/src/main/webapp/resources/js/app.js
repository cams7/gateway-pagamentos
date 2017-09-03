function openShopline(codigoEmpresa, codigoPedido, valorPedido) {
	console.log("openShopline: codigoEmpresa = " + codigoEmpresa + ", codigoPedido = " + codigoPedido + ", valorPedido = " + valorPedido);
	
	var formShopline = $("div#shopline").find("form");
	formShopline.find("input[name='DC']").val(codigoEmpresa.toUpperCase() + ";" + codigoPedido + ";" + valorPedido);
	formShopline.submit();
}

function carregabrw() {
	window	.open("", "SHOPLINE", "toolbar = yes,menubar=yes,resizable=yes,status=no,sc rollbars=yes,width=815,height=575");
}