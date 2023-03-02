import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConversorMoedas {
    public static void main(String[] args) {
        try {
            String moedaOrigem, moedaDestino;
            double valor;

            // Leitura da entrada do usuário
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Informe a moeda de origem (BRL, USD, EUR): ");
            moedaOrigem = br.readLine();
            System.out.print("Informe a moeda de destino (BRL, USD, EUR): ");
            moedaDestino = br.readLine();
            System.out.print("Informe o valor a ser convertido: ");
            valor = Double.parseDouble(br.readLine());

            // Montagem da URL da API de conversão do Banco Central
            String urlStr = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/" +
                    "CotacaoMoedaDia(moeda=@moeda,dataCotacao=@data)?@moeda='" +
                    moedaDestino + "'&@data='" + getDataAtual() + "'&$top=1&$format=json";
            URL url = new URL(urlStr);

            // Realização da requisição HTTP para a API
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Extração do valor de cotação da resposta da API
            String json = response.toString();
            int index = json.indexOf("cotacaoCompra");
            String cotacao = json.substring(index+15, index+21);

            // Cálculo da conversão e exibição do resultado
            double valorConvertido = Double.parseDouble(cotacao) * valor;
            System.out.printf("%.2f %s equivalem a %.2f %s", valor, moedaOrigem, valorConvertido, moedaDestino);
        } catch (Exception e) {
            System.out.println("Erro ao realizar a conversão: " + e.getMessage());
        }
    }

    // Método para obter a data atual no formato utilizado pela API do Banco Central
    private static String getDataAtual() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM-dd-yyyy");
        return dateFormat.format(calendar.getTime());
    }
}
