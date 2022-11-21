INSERT INTO roles(name)
VALUES ('ROLE_USER'), ('ROLE_ADMIN');

INSERT INTO users(username, password, email)
VALUES ('test', '$2a$10$p4Q3QoeR.tZLLGUA5o2bfeHx3P4zWL3w7YiP96Y6czQ8oxpEnlXnG', 'test@test.ru'), ('user', '$2a$10$WyNFbjq2t1aGPN.n1NrPcODeh35Du2u1jBj2xwWbQIefJq5zYdh6m', 'user@user.ru');

INSERT INTO user_roles(user_id, role_id)
VALUES (1, 2), (2, 1);