package br.com.cams7.utils;

/**
 * @author César Magalhães
 *
 */
public class Cep {

	public static boolean isValido(String cep) {
		if (cep == null || cep.isEmpty())
			return false;

		cep = desformata(cep);

		return cep.matches("[0-9]{8}");
	}

	public static String desformata(String cep) {
		return cep.replaceAll("^(\\d{5})\\-(\\d{3})$", "$1$2");
	}

	public static String formata(String cep) {
		return cep.replaceAll("^(\\d{5})(\\d{3})$", "$1-$2");
	}
}
