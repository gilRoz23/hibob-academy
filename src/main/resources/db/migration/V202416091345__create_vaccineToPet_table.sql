create table vaccine_to_pet
(
    id bigserial primary key,
    pet_id INT NOT NULL,
    vaccination_date DATE default current_date
);