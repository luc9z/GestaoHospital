package org.example;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DataInserter dataInserter = new DataInserter();
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1: Inserir dados");
            System.out.println("2: Deletar todas as inserções");
            System.out.println("3: Adicionar horários disponíveis para um médico");
            System.out.println("4: Sair");
            input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.println("Iniciando inserção de dados...");
                    dataInserter.insertData();
                    System.out.println("Inserção de dados concluída.");
                    break;

                case "2":
                    System.out.println("Deletando todas as inserções...");
                    dataInserter.deleteAllData();
                    System.out.println("Todas as inserções foram deletadas.");
                    break;

                case "3":
                    System.out.println("Digite o ID do médico:");
                    int doctorId = Integer.parseInt(scanner.nextLine());
                    List<Time> times = new ArrayList<>();
                    String timeInput;
                    System.out.println("Digite os horários disponíveis no formato HH:MM:SS (digite 'done' para terminar):");

                    while (true) {
                        timeInput = scanner.nextLine();
                        if ("done".equalsIgnoreCase(timeInput)) {
                            break;
                        }
                        times.add(Time.valueOf(timeInput));
                    }

                    if (!times.isEmpty()) {
                        dataInserter.addAvailableTimes(doctorId, times);
                        System.out.println("Horários adicionados com sucesso.");
                    } else {
                        System.out.println("Nenhum horário foi adicionado.");
                    }
                    break;

                case "4":
                    System.out.println("Saindo...");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Opção inválida. Por favor, tente novamente.");
            }
        }
    }
}
