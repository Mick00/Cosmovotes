package fr.station47.cosmovotes;

public enum ApiOp {
    VERIFY("verify"),
    CLAIM("claim");

    String op;
    ApiOp(String op){
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
