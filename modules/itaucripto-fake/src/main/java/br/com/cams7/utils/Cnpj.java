/**
 * 
 */
package br.com.cams7.utils;

import java.util.Arrays;

/**
 * @author César Magalhães
 *
 */
public class Cnpj {

	/**
	 * Verifica se o CNPJ e válido
	 * 
	 * @param cnpj
	 * @return
	 */
	public static boolean isValido(String cnpj) {
		if (cnpj == null || cnpj.isEmpty())
			return false;

		cnpj = desformata(cnpj);

		String[] cnpjInvalidos = { "00000000000000", "11111111111111", "22222222222222", "33333333333333",
				"44444444444444", "55555555555555", "66666666666666", "77777777777777", "88888888888888",
				"99999999999999" };

		// considera-se erro CNPJ's formados por uma sequencia de numeros iguais
		if (cnpj.length() != 14 || Arrays.asList(cnpjInvalidos).contains(cnpj))
			return false;

		char dig13, dig14;
		int sm, i, r, num, peso;

		// "try" - protege o código para eventuais erros de conversao de tipo
		// (int)

		// Calculo do 1o. Digito Verificador
		sm = 0;
		peso = 2;
		for (i = 11; i >= 0; i--) {
			// converte o i-ésimo caractere do CNPJ em um número:
			// por exemplo, transforma o caractere '0' no inteiro 0
			// (48 eh a posição de '0' na tabela ASCII)
			num = (int) (cnpj.charAt(i) - 48);
			sm = sm + (num * peso);
			peso = peso + 1;
			if (peso == 10)
				peso = 2;
		}

		r = sm % 11;
		if ((r == 0) || (r == 1))
			dig13 = '0';
		else
			dig13 = (char) ((11 - r) + 48);

		// Calculo do 2o. Digito Verificador
		sm = 0;
		peso = 2;
		for (i = 12; i >= 0; i--) {
			num = (int) (cnpj.charAt(i) - 48);
			sm = sm + (num * peso);
			peso = peso + 1;
			if (peso == 10)
				peso = 2;
		}

		r = sm % 11;
		if ((r == 0) || (r == 1))
			dig14 = '0';
		else
			dig14 = (char) ((11 - r) + 48);

		// Verifica se os dígitos calculados conferem com os dígitos
		// informados.
		if ((dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13)))
			return true;

		return false;
	}

	public static String desformata(String cnpj) {
		return cnpj.replaceAll("^(\\d{2})\\.(\\d{3})\\.(\\d{3})\\/(\\d{4})\\-(\\d{2})$", "$1$2$3$4$5");
	}

	public static String formata(String cnpj) {
		return cnpj.replaceAll("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})$", "$1.$2.$3/$4-$5");
	}

}
