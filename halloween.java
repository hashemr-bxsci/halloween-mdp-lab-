import pygame
import random

pygame.init()

WIDTH = 800
HEIGHT = 600
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Halloween Monster Ambush")
clock = pygame.time.Clock()

BLACK = (20, 18, 30)
WHITE = (255, 255, 245)
GRAY = (55, 53, 65)
GREEN = (80, 255, 120)
RED = (255, 75, 75)

GRAVEYARD_COLOR = (100, 105, 110)
HAUNTED_COLOR = (75, 0, 130)
FOREST_COLOR = (34, 139, 34)
PUMPKIN_COLOR = (255, 140, 0)

states = ["Graveyard", "Haunted House", "Forest", "Pumpkin Patch"]
colors = [GRAVEYARD_COLOR, HAUNTED_COLOR, FOREST_COLOR, PUMPKIN_COLOR]
rewards = [10, 5, 8, 20]

transition_matrix = [
    [0.10, 0.45, 0.30, 0.15],
    [0.35, 0.10, 0.40, 0.15],
    [0.25, 0.35, 0.15, 0.25],
    [0.40, 0.20, 0.20, 0.20]
]

locations = [
    pygame.Rect(0, 0, 400, 200),
    pygame.Rect(400, 0, 400, 200),
    pygame.Rect(0, 200, 400, 200),
    pygame.Rect(400, 200, 400, 200)
]

monster_positions = [
    (200, 110),
    (600, 110),
    (200, 310),
    (600, 310)
]

font_title = pygame.font.SysFont("Georgia", 28, bold=True)
font_label = pygame.font.SysFont("Georgia", 23, bold=True)
font_text = pygame.font.SysFont("Georgia", 21, bold=True)

current_state = 2
round_count = 0
max_rounds = 10
human_score = 0
message = "Monster is hiding in: Forest | Press 1-4 to ambush!"
result = ""
last_prediction = ""
last_move = ""
game_over = False

running = True
while running:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

        if event.type == pygame.KEYDOWN and not game_over:
            human_prediction = -1

            if event.key == pygame.K_1:
                human_prediction = 0
            elif event.key == pygame.K_2:
                human_prediction = 1
            elif event.key == pygame.K_3:
                human_prediction = 2
            elif event.key == pygame.K_4:
                human_prediction = 3

            if human_prediction != -1:
                row = transition_matrix[current_state]
                next_state = random.choices(range(4), weights=row, k=1)[0]

                if human_prediction == next_state:
                    human_score += 1
                    result = "Ambush successful! The monster was caught!"
                else:
                    result = "The monster escaped into the night!"

                last_prediction = "You chose: " + states[human_prediction]
                last_move = "Monster moved to: " + states[next_state]
                current_state = next_state
                message = "Monster is hiding in: " + states[current_state] + " | Pick 1-4!"
                round_count += 1

                if round_count == max_rounds:
                    game_over = True

    screen.fill(BLACK)

    for i in range(4):
        pygame.draw.rect(screen, colors[i], locations[i])
        pygame.draw.rect(screen, BLACK, locations[i], 3)

    pygame.draw.line(screen, BLACK, (400, 0), (400, 400), 5)
    pygame.draw.line(screen, BLACK, (0, 200), (800, 200), 5)

    labels = ["GRAVEYARD (1) +10", "HAUNTED HOUSE (2) +5", "DARK FOREST (3) +8", "PUMPKIN PATCH (4) +20"]
    for i in range(4):
        label = font_label.render(labels[i], True, WHITE)
        screen.blit(label, (locations[i].x + 18, locations[i].y + 18))

    monster_x, monster_y = monster_positions[current_state]
    pygame.draw.circle(screen, RED, (monster_x, monster_y), 28)
    pygame.draw.circle(screen, BLACK, (monster_x - 9, monster_y - 5), 4)
    pygame.draw.circle(screen, BLACK, (monster_x + 9, monster_y - 5), 4)
    pygame.draw.arc(screen, BLACK, (monster_x - 10, monster_y + 2, 20, 13), 3.14, 6.28, 3)
    monster_label = font_text.render("MONSTER", True, WHITE)
    screen.blit(monster_label, (monster_x - monster_label.get_width() // 2, monster_y + 35))

    pygame.draw.rect(screen, GRAY, (0, 400, WIDTH, 200))
    pygame.draw.line(screen, WHITE, (0, 400), (WIDTH, 400), 3)

    if game_over:
        title = font_title.render("GAME OVER - 10 ROUNDS COMPLETE", True, RED)
        screen.blit(title, (25, 412))
        screen.blit(font_text.render("Successful ambushes: " + str(human_score) + "/10", True, WHITE), (25, 452))
        screen.blit(font_text.render("Happy Halloween, monster hunter!", True, GREEN), (25, 492))
    else:
        screen.blit(font_text.render(message, True, WHITE), (25, 412))
        screen.blit(font_text.render(last_prediction, True, WHITE), (25, 448))
        screen.blit(font_text.render(last_move, True, WHITE), (25, 478))

        result_color = GREEN if "successful" in result else RED
        screen.blit(font_text.render(result, True, result_color), (25, 508))

        score = "Round: " + str(round_count) + "/10   Successful ambushes: " + str(human_score)
        screen.blit(font_text.render(score, True, WHITE), (25, 538))

        expected = []
        for i in range(4):
            expected.append(transition_matrix[current_state][i] * rewards[i])
        best_action = expected.index(max(expected))
        smart_pick = "Smart pick: " + states[best_action] + " (expected reward " + str(round(expected[best_action], 1)) + ")"
        screen.blit(font_text.render(smart_pick, True, GREEN), (25, 568))

    pygame.display.flip()
    clock.tick(30)

pygame.quit()
