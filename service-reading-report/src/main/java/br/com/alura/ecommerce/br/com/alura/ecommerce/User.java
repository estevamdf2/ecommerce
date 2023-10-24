package br.com.alura.ecommerce.br.com.alura.ecommerce;

public class User {
    private String uuid;

    public User(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getReportPath(){
        return "target/" + uuid + "-report.txt";
    }
}
