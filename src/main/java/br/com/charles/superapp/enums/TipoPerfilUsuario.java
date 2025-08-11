package br.com.charles.superapp.enums;

import lombok.Getter;

@Getter
public enum TipoPerfilUsuario {

    SERVIDOR_PUBLICO(1, "Servidor Público"),
    EMPRESARIO(2, "Empresário"),
    ESTUDANTE(3, "Estudante"),
    APOSENTADO(4, "Aposentado"),
    AUTONOMO(5, "Autônomo"),
    DESEMPREGADO(6, "Desempregado"),
    OUTRO(99, "Outro");

    private final int codigo;
    private final String descricao;

    TipoPerfilUsuario(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static TipoPerfilUsuario fromCodigo(int codigo) {
        for (TipoPerfilUsuario perfil : TipoPerfilUsuario.values()) {
            if (perfil.codigo == codigo) {
                return perfil;
            }
        }
        throw new IllegalArgumentException("Código de perfil inválido: " + codigo);
    }
}
