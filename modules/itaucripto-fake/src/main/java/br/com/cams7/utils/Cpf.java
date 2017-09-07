/**
 * 
 */
package br.com.cams7.utils;

import java.util.Arrays;

/**
 * @author César Magalhães
 *
 */
public class Cpf {

	/**
	 * Verifica se o CPF e valido
	 * 
	 * @param cpf
	 * @return
	 */
	public static boolean isValido(String cpf) {
		if (cpf == null || cpf.isEmpty())
			return false;

		cpf = desformata(cpf);

		String[] cpfInvalidos = { "00000000000", "11111111111", "22222222222", "33333333333", "44444444444",
				"55555555555", "66666666666", "77777777777", "88888888888", "99999999999" };

		if (cpf.length() != 11 || Arrays.asList(cpfInvalidos).contains(cpf))
			return false;

		final int[] pesoCPF = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

		int digito1 = calculaDigito(cpf.substring(0, 9), pesoCPF);
		int digito2 = calculaDigito(cpf.substring(0, 9) + digito1, pesoCPF);
		return cpf.equals(cpf.substring(0, 9) + String.valueOf(digito1) + String.valueOf(digito2));
	}

	private static int calculaDigito(String cpf, int[] peso) {
		int soma = 0;
		for (int indice = cpf.length() - 1, digito; indice >= 0; indice--) {
			digito = Integer.parseInt(cpf.substring(indice, indice + 1));
			soma += digito * peso[peso.length - cpf.length() + indice];
		}
		soma = 11 - soma % 11;
		return soma > 9 ? 0 : soma;
	}

	public static String desformata(String cpf) {
		return cpf.replaceAll("^(\\d{3})\\.(\\d{3})\\.(\\d{3})\\-(\\d{2})$", "$1$2$3$4");
	}

	public static String formata(String cpf) {
		return cpf.replaceAll("^(\\d{3})(\\d{3})(\\d{3})(\\d{2})$", "$1.$2.$3-$4");
	}

}
