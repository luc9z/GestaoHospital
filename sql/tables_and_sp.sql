CREATE TABLE Patients (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  birth_date DATE NOT NULL,
  contact VARCHAR(50) NOT NULL
);

CREATE TABLE Doctors (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  specialty VARCHAR(50) NOT NULL,
  schedule TIME[] NOT NULL,
  availability BOOLEAN NOT NULL
);

CREATE TABLE Appointments (
  id SERIAL PRIMARY KEY,
  date DATE NOT NULL,
  time TIME NOT NULL,
  patient_id INTEGER NOT NULL REFERENCES Patients(id),
  doctor_id INTEGER NOT NULL REFERENCES Doctors(id)
);

CREATE TABLE MedicalHistory (
  id SERIAL PRIMARY KEY,
  patient_id INTEGER NOT NULL REFERENCES Patients(id),
  diagnosis TEXT,
  treatment TEXT,
  prescription TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE PROCEDURE ScheduleAppointment(
    IN p_date DATE,
    IN p_time TIME,
    IN p_patient_id INTEGER,
    IN p_doctor_id INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    doctor_available BOOLEAN;
    start_time TIMESTAMP;
    end_time TIMESTAMP;
BEGIN
    start_time := clock_timestamp();
    
    SELECT availability
    INTO doctor_available
    FROM Doctors
    WHERE id = p_doctor_id
    AND p_time = ANY(schedule)
    AND availability = TRUE;

    IF doctor_available THEN
        INSERT INTO Appointments (date, time, patient_id, doctor_id)
        VALUES (p_date, p_time, p_patient_id, p_doctor_id);
        
        UPDATE Doctors
        SET availability = FALSE
        WHERE id = p_doctor_id;

        RAISE NOTICE 'Consulta agendada com sucesso';
    ELSE
        RAISE EXCEPTION 'Médico não está disponível no horário solicitado';
    END IF;
    
    end_time := clock_timestamp();
    RAISE NOTICE 'Tempo de execução: % ms', EXTRACT(MILLISECOND FROM end_time - start_time);
END;
$$;

CREATE OR REPLACE PROCEDURE CancelAppointment(
    IN p_appointment_id INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_doctor_id INTEGER;
    v_time TIME;
    v_date DATE;
    start_time TIMESTAMP;
    end_time TIMESTAMP;
BEGIN
    start_time := clock_timestamp();

    SELECT doctor_id, time, date
    INTO v_doctor_id, v_time, v_date
    FROM Appointments
    WHERE id = p_appointment_id;

    DELETE FROM Appointments
    WHERE id = p_appointment_id;

    UPDATE Doctors
    SET availability = TRUE
    WHERE id = v_doctor_id;

    RAISE NOTICE 'Consulta cancelada com sucesso';

    end_time := clock_timestamp();
    RAISE NOTICE 'Tempo de execução: % ms', EXTRACT(MILLISECOND FROM end_time - start_time);
END;
$$;

CREATE OR REPLACE PROCEDURE UpdateMedicalHistory(
    IN p_patient_id INTEGER,
    IN p_diagnosis TEXT,
    IN p_treatment TEXT,
    IN p_prescription TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    start_time TIMESTAMP;
    end_time TIMESTAMP;
BEGIN
    start_time := clock_timestamp();

    INSERT INTO MedicalHistory (patient_id, diagnosis, treatment, prescription)
    VALUES (p_patient_id, p_diagnosis, p_treatment, p_prescription);

    RAISE NOTICE 'Histórico médico atualizado com sucesso';

    end_time := clock_timestamp();
    RAISE NOTICE 'Tempo de execução: % ms', EXTRACT(MILLISECOND FROM end_time - start_time);
END;
$$;
