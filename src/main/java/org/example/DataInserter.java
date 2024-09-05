package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;

public class DataInserter {

    private static final int BATCH_SIZE = 250000;

    public void insertData() {
        Connection connection = null;
        PreparedStatement psInsertPatient = null;
        PreparedStatement psInsertDoctor = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);
            String sqlInsertPatient = "INSERT INTO Patients (name, birth_date, contact) VALUES (?, ?, ?)";
            String sqlInsertDoctor = "INSERT INTO Doctors (name, specialty, schedule, availability) VALUES (?, ?, ARRAY[?], ?)";

            psInsertPatient = connection.prepareStatement(sqlInsertPatient);
            psInsertDoctor = connection.prepareStatement(sqlInsertDoctor);

            for (int batch = 1; batch <= 4; batch++) {
                List<String[]> patientData = new ArrayList<>();
                List<String[]> doctorData = new ArrayList<>();

                for (int i = 1; i <= BATCH_SIZE; i++) {
                    int id = (batch - 1) * BATCH_SIZE + i;
                    patientData.add(new String[]{String.format("Paciente %d", id), String.valueOf(new java.sql.Date(System.currentTimeMillis())), String.format("Contato %d", id)});
                    doctorData.add(new String[]{String.format("Médico %d", id), String.format("Especialidade %d", id), String.valueOf(new java.sql.Time(System.currentTimeMillis())), "true"});
                }

                Savepoint savepoint = connection.setSavepoint("BeforeInsertBatch" + batch);

                for (int i = 0; i < BATCH_SIZE; i++) {
                    psInsertPatient.setString(1, patientData.get(i)[0]);
                    psInsertPatient.setDate(2, java.sql.Date.valueOf(patientData.get(i)[1]));
                    psInsertPatient.setString(3, patientData.get(i)[2]);
                    psInsertPatient.addBatch();

                    psInsertDoctor.setString(1, doctorData.get(i)[0]);
                    psInsertDoctor.setString(2, doctorData.get(i)[1]);
                    psInsertDoctor.setArray(3, connection.createArrayOf("TIME", new java.sql.Time[]{java.sql.Time.valueOf(doctorData.get(i)[2])}));
                    psInsertDoctor.setBoolean(4, Boolean.parseBoolean(doctorData.get(i)[3]));
                    psInsertDoctor.addBatch();
                }

                psInsertPatient.executeBatch();
                psInsertDoctor.executeBatch();
                connection.commit();

                System.out.println("Lote " + batch + " inserido com sucesso.");
            }

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
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAllData() {
        Connection connection = null;
        PreparedStatement psDeletePatients = null;
        PreparedStatement psDeleteDoctors = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String sqlDeletePatients = "TRUNCATE TABLE Patients CASCADE";
            String sqlDeleteDoctors = "TRUNCATE TABLE Doctors CASCADE";

            psDeletePatients = connection.prepareStatement(sqlDeletePatients);
            psDeleteDoctors = connection.prepareStatement(sqlDeleteDoctors);

            psDeletePatients.executeUpdate();
            psDeleteDoctors.executeUpdate();

            connection.commit();
            System.out.println("Todas as inserções foram deletadas com sucesso.");

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
                if (psDeletePatients != null) psDeletePatients.close();
                if (psDeleteDoctors != null) psDeleteDoctors.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addAvailableTimes(int doctorId, List<Time> newTimes) {
        Connection connection = null;
        PreparedStatement psUpdateDoctor = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String sqlUpdateDoctor = "UPDATE Doctors " +
                    "SET schedule = array_cat(schedule, ?) " + 
                    "WHERE id = ?";

            psUpdateDoctor = connection.prepareStatement(sqlUpdateDoctor);

            Time[] timeArray = newTimes.toArray(new Time[0]);
            psUpdateDoctor.setArray(1, connection.createArrayOf("TIME", timeArray));
            psUpdateDoctor.setInt(2, doctorId);

            psUpdateDoctor.executeUpdate();
            connection.commit();

            System.out.println("Horários disponíveis adicionados com sucesso para o médico com ID " + doctorId);

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
                if (psUpdateDoctor != null) psUpdateDoctor.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
