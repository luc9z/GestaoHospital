# Sistema de Gestão de Clínica Médica

## Descrição do Projeto
Este projeto consiste no desenvolvimento de um sistema de gestão para uma clínica médica, que permite o gerenciamento de pacientes, médicos, agendamentos de consultas e históricos médicos. O sistema foi projetado para facilitar a administração de uma clínica, garantindo eficiência no processo de agendamento e manutenção dos registros médicos.

## Tabelas

### 1. **Patients**
Armazena informações dos pacientes da clínica.

### 2. **Doctors**
Mantém dados sobre os médicos da clínica, incluindo especialidade e horários de atendimento.

### 3. **Appointments**
Registra os agendamentos de consultas, associando pacientes e médicos.

### 4. **MedicalHistory**
Armazena os históricos médicos dos pacientes, incluindo diagnósticos, tratamentos e prescrições.

## Procedures

### 1. **ScheduleAppointment**
Procedure para agendar uma nova consulta. Verifica a disponibilidade do médico para a data e hora solicitadas e, se disponível, registra o agendamento.

### 2. **CancelAppointment**
Procedure que cancela um agendamento existente, liberando o horário do médico para que novas consultas possam ser agendadas.

### 3. **UpdateMedicalHistory**
Procedure que atualiza o histórico médico de um paciente, inserindo novos registros de diagnósticos, tratamentos e prescrições.

## Informações Acadêmicas
- **Curso**: 30-838 Programação em Banco de Dados
- **Professor**: Gustavo Girardon dos Reis
- **Grupo**: Grupo 2 - Sistema de Gestão de Clínica Médica