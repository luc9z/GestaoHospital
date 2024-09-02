package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;

public class DataInserter {

    private static final int BATCH_SIZE = 250000;

    public void insertData() {
        Connection connection = null;
        PreparedStatement psInsertPatient = null;
        PreparedStatement psInsertDoctor = null;
        PreparedStatement psInsertAppointment = null;
        PreparedStatement psInsertMedicalHistory = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Desativar commit automático

            // Criação dos SQLs para inserção
            String sqlInsertPatient = "INSERT INTO Patients (name, birth_date, contact) VALUES (?, ?, ?)";
            String sqlInsertDoctor = "INSERT INTO Doctors (name, specialty, schedule, availability) VALUES (?, ?, ARRAY[?], ?)";
            String sqlInsertAppointment = "INSERT INTO Appointments (date, time, patient_id, doctor_id) VALUES (?, ?, ?, ?)";
            String sqlInsertMedicalHistory = "INSERT INTO MedicalHistory (patient_id, diagnosis, treatment, prescription) VALUES (?, ?, ?, ?)";

            psInsertPatient = connection.prepareStatement(sqlInsertPatient);
            psInsertDoctor = connection.prepareStatement(sqlInsertDoctor);
            psInsertAppointment = connection.prepareStatement(sqlInsertAppointment);
            psInsertMedicalHistory = connection.prepareStatement(sqlInsertMedicalHistory);

            List<String[]> patientData = new ArrayList<>();
            List<String[]> doctorData = new ArrayList<>();
            List<String[]> appointmentData = new ArrayList<>();
            List<String[]> medicalHistoryData = new ArrayList<>();

            // Gerar dados
            for (int i = 1; i <= 500000; i++) {
                patientData.add(new String[]{String.format("Paciente %d", i), String.valueOf(new java.sql.Date(System.currentTimeMillis())), String.format("Contato %d", i)});
                doctorData.add(new String[]{String.format("Médico %d", i), String.format("Especialidade %d", i), String.valueOf(new java.sql.Time(System.currentTimeMillis())), "true"});
                appointmentData.add(new String[]{String.valueOf(new java.sql.Date(System.currentTimeMillis())), String.valueOf(new java.sql.Time(System.currentTimeMillis())), String.valueOf(i), String.valueOf(i)});
                medicalHistoryData.add(new String[]{String.valueOf(i), String.format("Diagnóstico %d", i), String.format("Tratamento %d", i), String.format("Prescrição %d", i)});
            }

            Savepoint savepoint = connection.setSavepoint("BeforeInsert"); // Criar savepoint antes das inserções

            // Inserir em Patients e Doctors primeiro
            for (int i = 0; i < patientData.size(); i++) {
                // Inserir em Patients
                psInsertPatient.setString(1, patientData.get(i)[0]);
                psInsertPatient.setDate(2, java.sql.Date.valueOf(patientData.get(i)[1]));
                psInsertPatient.setString(3, patientData.get(i)[2]);
                psInsertPatient.addBatch();

                // Inserir em Doctors
                psInsertDoctor.setString(1, doctorData.get(i)[0]);
                psInsertDoctor.setString(2, doctorData.get(i)[1]);
                psInsertDoctor.setArray(3, connection.createArrayOf("TIME", new java.sql.Time[]{java.sql.Time.valueOf(doctorData.get(i)[2])}));
                psInsertDoctor.setBoolean(4, Boolean.parseBoolean(doctorData.get(i)[3]));
                psInsertDoctor.addBatch();

                if ((i + 1) % BATCH_SIZE == 0 || i == patientData.size() - 1) {
                    psInsertPatient.executeBatch();
                    psInsertDoctor.executeBatch();
                    connection.commit();
                }
            }

            // Agora, insira em Appointments e MedicalHistory
            for (int i = 0; i < appointmentData.size(); i++) {
                // Inserir em Appointments
                psInsertAppointment.setDate(1, java.sql.Date.valueOf(appointmentData.get(i)[0]));
                psInsertAppointment.setTime(2, java.sql.Time.valueOf(appointmentData.get(i)[1]));
                psInsertAppointment.setInt(3, Integer.parseInt(appointmentData.get(i)[2]));
                psInsertAppointment.setInt(4, Integer.parseInt(appointmentData.get(i)[3]));
                psInsertAppointment.addBatch();

                // Inserir em MedicalHistory
                psInsertMedicalHistory.setInt(1, Integer.parseInt(medicalHistoryData.get(i)[0]));
                psInsertMedicalHistory.setString(2, medicalHistoryData.get(i)[1]);
                psInsertMedicalHistory.setString(3, medicalHistoryData.get(i)[2]);
                psInsertMedicalHistory.setString(4, medicalHistoryData.get(i)[3]);
                psInsertMedicalHistory.addBatch();

                if ((i + 1) % BATCH_SIZE == 0 || i == appointmentData.size() - 1) {
                    psInsertAppointment.executeBatch();
                    psInsertMedicalHistory.executeBatch();
                    connection.commit();
                }
            }

            System.out.println("Inserções bem-sucedidas.");

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (psInsertPatient != null) psInsertPatient.close();
                if (psInsertDoctor != null) psInsertDoctor.close();
                if (psInsertAppointment != null) psInsertAppointment.close();
                if (psInsertMedicalHistory != null) psInsertMedicalHistory.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
