package Desafio.Fipe.main;

import Desafio.Fipe.model.Data;
import Desafio.Fipe.model.Models;
import Desafio.Fipe.model.Vehicle;
import Desafio.Fipe.service.APIconsumer;
import Desafio.Fipe.service.ConvertData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private Scanner scanner = new Scanner(System.in);

    private APIconsumer apIconsumer = new APIconsumer();
    private ConvertData convertData = new ConvertData();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void displayMenu(){

        String url;
        var option = " ";

        var menu = """
                **** Opções ****
                Carros
                Motos
                Caminhoes
                
                Digite uma das opções acima:
                """;

        do {
            System.out.println(menu);
            option = scanner.nextLine();
        } while (!option.contains("carr") && !option.contains("mot") && !option.contains("caminh"));


        if (option.toLowerCase().contains("carr")){
            url = URL_BASE + "carros/marcas";
        } else if (option.toLowerCase().contains("mot")){
            url = URL_BASE + "motos/marcas";
        } else {
            url = URL_BASE + "caminhoes/marcas";
        }

        var json = apIconsumer.getData(url);

        var models = convertData.getList(json, Data.class);

        models.stream()
                .sorted(Comparator.comparing(Data::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca que você deseja: ");
        var codeBrand = scanner.nextLine();

        url = url + "/" + codeBrand + "/modelos";

        json = apIconsumer.getData(url);

        var modelList = convertData.getData(json, Models.class);

        System.out.println("\nModelos:");

        modelList.modelos().stream()
                .sorted(Comparator.comparing(Data::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro que você deseja: ");;
        var vehicleName = scanner.nextLine();

        List<Data> filterModels = modelList.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(vehicleName.toLowerCase()))
                .collect(Collectors.toList());


        System.out.println("\nModelos filtrados");
        filterModels.forEach(System.out::println);

        System.out.println("\nInforme o código do modelo: ");
        var vehicleCode = scanner.nextLine();

        url = url + "/" + vehicleCode + "/anos";

        json = apIconsumer.getData(url);

        List<Data> years = convertData.getList(json, Data.class);

        List<Vehicle> vehicles = new ArrayList<>();

        for (int i = 0; i < years.size(); i++) {
            var finalUrl = url + "/" + years.get(i).codigo();
            json = apIconsumer.getData(finalUrl);

            Vehicle vehicle = convertData.getData(json, Vehicle.class);
            vehicles.add(vehicle);
        }

        System.out.println("\nLista de veículos: ");

        vehicles.forEach(System.out::println);
    }
}
