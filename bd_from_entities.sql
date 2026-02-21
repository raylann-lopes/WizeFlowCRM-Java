-- =====================================================
-- SQL GERADO A PARTIR DAS ENTIDADES JPA
-- Data: 21/02/2026
-- Fonte de verdade: Entidades Java (pasta entity)
-- =====================================================



-- =============================================
-- TABLE: companies
-- =====================================================

CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(11) NOT NULL,
    settings JSONB,
    plan VARCHAR(50) DEFAULT 'free',
    status VARCHAR(255) DEFAULT 'active',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_companies_slug ON companies(slug);
CREATE INDEX idx_companies_status ON companies(status);
CREATE INDEX idx_companies_plan ON companies(plan);

CREATE TRIGGER trg_companies_updated
    BEFORE UPDATE ON companies
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- =====================================================
-- TABLE: users
-- =====================================================

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    companies_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    cpf VARCHAR(255) UNIQUE,
    phone VARCHAR(255),
    job_title VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    session_id VARCHAR(255),
    department VARCHAR(255),
    CONSTRAINT users_companies_id_fkey
        FOREIGN KEY (companies_id)
            REFERENCES companies(id)
            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE UNIQUE INDEX users_companies_email_key ON users(companies_id, email);
CREATE INDEX idx_users_companies_id ON users(companies_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_session_id ON users(session_id);
CREATE INDEX idx_users_role ON users(role);

-- =====================================================
-- TABLE: clients
-- =====================================================

CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    companies_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    business_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20) NOT NULL,
    status VARCHAR(255) DEFAULT 'Ativo',
    notes VARCHAR(255),
    session_id VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_interaction TIMESTAMPTZ,
    CONSTRAINT clients_companies_id_fkey
        FOREIGN KEY (companies_id)
            REFERENCES companies(id)
            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_client_companies_id ON clients(companies_id);
CREATE INDEX idx_client_companies_status ON clients(companies_id, status);
CREATE INDEX idx_client_session_id ON clients(session_id);

-- =====================================================
-- TABLE: chat_sessions
-- =====================================================

CREATE TABLE chat_sessions (
    id SERIAL PRIMARY KEY,
    companies_id INT NOT NULL,
    client_id INT,
    user_id INT,
    channel VARCHAR(255) DEFAULT 'WHATSAPP',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chat_sessions_companies_id_fkey
        FOREIGN KEY (companies_id)
            REFERENCES companies(id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chat_sessions_client_id_fkey
        FOREIGN KEY (client_id)
            REFERENCES clients(id),
    CONSTRAINT chat_sessions_user_id_fkey
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);

CREATE INDEX idx_chat_session_companies_id ON chat_sessions(companies_id);
CREATE INDEX idx_chat_session_client_id ON chat_sessions(client_id);
CREATE INDEX idx_chat_user_id ON chat_sessions(user_id);

CREATE TRIGGER trg_chat_sessions_updated
    BEFORE UPDATE ON chat_sessions
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- =====================================================
-- TABLE: leads
-- =====================================================

CREATE TABLE leads (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL,
    companies_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    business_name VARCHAR(255),
    status VARCHAR(255) DEFAULT 'Novo',
    description VARCHAR(255),
    value FLOAT,
    is_hot BOOLEAN DEFAULT false,
    is_delayed BOOLEAN DEFAULT false,
    last_activity TIMESTAMPTZ,
    email VARCHAR(255),
    phone VARCHAR(255),
    job_title VARCHAR(255),
    industry VARCHAR(255),
    source VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    session_id INT,
    CONSTRAINT leads_companies_id_fkey
        FOREIGN KEY (companies_id)
            REFERENCES companies(id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT leads_client_id_fkey
        FOREIGN KEY (client_id)
            REFERENCES clients(id),
    CONSTRAINT leads_session_id_fkey
        FOREIGN KEY (session_id)
            REFERENCES chat_sessions(id)
);

CREATE INDEX idx_leads_companies_id ON leads(companies_id);
CREATE INDEX idx_leads_client_id ON leads(client_id);
CREATE INDEX idx_leads_companies_status ON leads(companies_id, status);
CREATE INDEX idx_leads_session_id ON leads(session_id);

-- =====================================================
-- TABLE: tickets
-- =====================================================

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    companies_id INT NOT NULL,
    user_id INT NOT NULL,
    subject VARCHAR(255),
    department VARCHAR(255),
    urgency VARCHAR(255) DEFAULT 'Baixa',
    status VARCHAR(255) DEFAULT 'Aberto',
    message VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tickets_companies_id_fkey
        FOREIGN KEY (companies_id)
            REFERENCES companies(id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT tickets_user_id_fkey
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_tickets_companies_id ON tickets(companies_id);
CREATE INDEX idx_tickets_user_id ON tickets(user_id);
CREATE INDEX idx_tickets_companies_status ON tickets(companies_id, status);

-- =====================================================
-- TABLE: appointments
-- =====================================================

CREATE TABLE appointments (
    id SERIAL PRIMARY KEY,
    companies_id INT NOT NULL,
    cliente_id INT,
    user_id INT,
    title VARCHAR(255),
    description VARCHAR(255),
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    appointment_status VARCHAR(255),
    session_id INT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT appointments_companies_id_fkey
        FOREIGN KEY (companies_id)
            REFERENCES companies(id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT appointments_cliente_id_fkey
        FOREIGN KEY (cliente_id)
            REFERENCES clients(id),
    CONSTRAINT appointments_user_id_fkey
        FOREIGN KEY (user_id)
            REFERENCES users(id),
    CONSTRAINT appointments_session_id_fkey
        FOREIGN KEY (session_id)
            REFERENCES chat_sessions(id)
);

CREATE INDEX idx_appointments_companies_id ON appointments(companies_id);
CREATE INDEX idx_appointments_companies_date ON appointments(companies_id, start_time);
CREATE INDEX idx_appointments_session_id ON appointments(session_id);

CREATE TRIGGER trg_appointments_updated
    BEFORE UPDATE ON appointments
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- =====================================================
-- TABLE: n8n_chat_histories
-- =====================================================

CREATE TABLE n8n_chat_histories (
    id SERIAL PRIMARY KEY,
    session_id VARCHAR(255),
    message JSONB
);


