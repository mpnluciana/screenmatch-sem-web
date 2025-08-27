package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodios;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://omdbapi.com/?t=";
    private final String API_KEY = "&apikey=5b43f1fc";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

//        for(int i = 0; i < dados.totalTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for(int j = 0; j< episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        /*System.out.println("Nome das minhas amigas... ");
        List<String> nomes = Arrays.asList( "Rafa", "Mica", "Gabi", "Olivia", "Nath");
        nomes.stream()
                .sorted() // coloca em ordem alfabética
                .limit(3) // limita objetos qual número que escolheu
                .filter(n -> n.startsWith("N")) //apenas objetos que começam com a letra desejada
                .map(n -> n.toUpperCase()) // deixa todas as letras em maiúsculo
                .forEach(System.out::println);
         */
        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
               .collect(Collectors.toList()); //para listas que precisam ser alteradas
               // .toList(); // lista imutável

//        System.out.println("\n Top 10 episodios mais avaliados:\n");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e-> System.out.println("Primeiro filtro (N/A): " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e-> System.out.println("Ordenação: " + e))
//                //reversed --> do maior ao menor
//                .limit(10)
//                .peek(e-> System.out.println("Limite: " + e))
//                .map(e-> e.titulo().toUpperCase())
//                .peek(e-> System.out.println("Mapeamento: " + e))
//                .forEach(System.out::println);

        List<Episodios> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodios(t.numero(), d))
                ).toList();

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do nome do seu episódio preferido: ");

        var trechoTitulo = leitura.nextLine();
        Optional<Episodios>episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();
        if(episodioBuscado.isPresent()){
            System.out.println("Episódio encontrado ^^ ");
            System.out.println("Nome: "+episodioBuscado.get().getTitulo());
            System.out.println("Temporada: "+episodioBuscado.get().getTemporada());
            System.out.println("Episódio: "+episodioBuscado.get().getNumeroEpisodio());
        } else{
            System.out.println("Espisódio não encontrado...");
        }
//
//        System.out.println("A partir de que ano você quer ver os episódios? ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1,1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() !=null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                "Episódio: " + e.getTitulo() +
//                                "Data de lançamento: " + e.getDataLancamento().format(formatador)
//                ));

//        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
//                .filter(e-> e.getAvaliacao()>0.0)
//                .collect(Collectors.groupingBy(Episodios::getTemporada,
//                        Collectors.averagingDouble(Episodios::getAvaliacao)));
//        System.out.println(avaliacoesPorTemporada);

        System.out.println("\n");
        System.out.println("Avaliações da série: ");
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e-> e.getAvaliacao()>0.0)
                .collect(Collectors.summarizingDouble(Episodios::getAvaliacao));
        System.out.println("Média das avaliações: "+est.getAverage());
        System.out.println("Episódio com maior avalialçao: "+est.getMax());
        System.out.println("Episódio com menor avaliação: "+est.getMin());
        System.out.println("Quantidade de avaliações: "+est.getCount());

    }
}