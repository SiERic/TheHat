from flask import Flask, request
from flask_restful import Resource, Api

import os
import json

class Player:
    def __init__(self, id, name, guessed = 0, explained = 0):
        self.id = id
        self.name = name
        self.guessed = guessed
        self.explained = explained

class Word:
    STATUS_UNUSED = 0
    STATUS_USED = 1
    STATUS_GUESSED = 2
    STATUS_FAILED = 3

    def __init__(self, word, time = 0, status = STATUS_UNUSED):
        self.word = word
        self.time = time
        self.status = status

    def status_name(self):
        return {0 : "UNUSED", 1 : "USED", 2 : "GUESSED", 3 : "FAILED"}[self.status]

class Game:
    STATUS_CREATED = 0
    STATUS_RUNNING = 1
    STATUS_FINISHED = 2

    def __init__(self, id):
        self.id = id
        self.status = Game.STATUS_CREATED
        self.players = []
        self.phase = 0
        self.words = []
        self.finished_words_ids = []
        self.is_square = True

    def current_players(self):
        first = self.phase % len(self.players)
        if self.is_square:
            second = (self.phase % len(self.players) + (self.phase // len(self.players)) % (len(self.players) - 1) + 1) % len(self.players)
        else:
            second = (self.phase + len(self.players) // 2) % len(self.players)

        return (self.players[first].id, self.players[second].id)

    def status_name(self):
        return {0 : "CREATED", 1 : "RUNNING", 2 : "FINISHED"}[self.status]


games = {}

app = Flask(__name__)
api = Api(app)

class HelloWorld(Resource):
    def get(self, who):
        return {"message": "{} is the best".format(who)}

api.add_resource(HelloWorld, "/hello/<string:who>")

def make_response(**kwargs):
    res = {"status": "OK"}
    for (k, v) in kwargs.items():
        res[k] = v
    return res

class CreateGame(Resource):
    def get(self, id):
        if id in games:
            return make_response(status="FAIL", message="game already exists")
        games[id] = Game(id)
        return make_response()

api.add_resource(CreateGame, "/create_game/<string:id>")


class GameStatus(Resource):
    def get(self, id):
        if id not in games:
            return make_response(status="FAIL", message="game doesn't exist")
        game = games[id]

        if game.status == Game.STATUS_RUNNING:
            current_players = game.current_players()
            return make_response(
                game_status=game.status_name(), first_player=current_players[0], second_player=current_players[1],
                finished_words=len(game.finished_words_ids), is_square=game.is_square, words_number=len(game.words),
                players_number=len(game.players)
            )
        else:
            return make_response(game_status=game.status_name(), words_number=len(game.words), players_number=len(game.players))

api.add_resource(GameStatus, "/game_status/<string:id>")

class AddWords(Resource):
    def post(self, game_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")
        game = games[game_id]
        json_data = request.data.decode("utf-8")
        data = json.loads(json_data)
        words = data["words"]
        game.words += [Word(word) for word in words]
        return make_response(message="got {} words".format(len(words)))

api.add_resource(AddWords, "/add_words/<string:game_id>")

class AddPlayer(Resource):
    def post(self, game_id, name):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]
        player_id = len(game.players)
        game.players.append(Player(player_id, name))

        return make_response(player_id=player_id)

api.add_resource(AddPlayer, "/add_player/<string:game_id>/<string:name>")

class FinishedWords(Resource):
    def get(self, game_id, finished_since_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]
        finished_ids = game.finished_words_ids[int(finished_since_id):]
        return make_response(finished_ids=finished_ids)

api.add_resource(FinishedWords, "/finished_word/<string:game_id>/<string:finished_since_id>")

class WordsStats(Resource):
    def get(self, game_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]
        stats = [{"time": word.time, "status": word.status_name()} for word in game.words]
        return make_response(stats=stats)

api.add_resource(WordsStats, "/words_stats/<string:game_id>")

class PlayersStats(Resource):
    def get(self, game_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]
        stats = [{"explained": player.explained, "guessed": player.guessed, "name": player.name} for player in game.players]
        return make_response(stats=stats)

api.add_resource(PlayersStats, "/players_stats/<string:game_id>")

class AllWords(Resource):
    def get(self, game_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]
        words = [{"word": word.word} for word in game.words]
        return make_response(words=words)

api.add_resource(AllWords, "/all_words/<string:game_id>")

class AllPlayers(Resource):
    def get(self, game_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]
        players = [{"id": player.id, "name": player.name} for player in game.players]
        return make_response(players=players)

api.add_resource(AllPlayers, "/all_players/<string:game_id>")

class StartGame(Resource):
    def post(self, game_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]
        game.status = Game.STATUS_RUNNING

        json_data = request.data.decode("utf-8")
        data = json.loads(json_data)
        game.is_square = data["is_square"]

        players_perm = data["players"]
        if len(players_perm) != len(game.players):
            return make_response(status="FAIL", message="incorrect players number")

        game.players = [game.players[players_perm[i]] for i in range(len(players_perm))]

        return make_response()

api.add_resource(StartGame, "/start_game/<string:game_id>")

class FinishGame(Resource):
    def post(self, game_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]
        game.status = Game.STATUS_FINISHED

        return make_response()

api.add_resource(FinishGame, "/finish_game/<string:game_id>")

class DoPhase(Resource):
    def post(self, game_id):
        if game_id not in games:
            return make_response(status="FAIL", message="game doesn't exists")

        game = games[game_id]

        first_player, second_player = game.current_players()


        json_data = request.data.decode("utf-8")
        data = json.loads(json_data)
        data_words = data["words"]

        for data_word in data_words:
            id = data_word["id"]
            status = data_word["status"]
            time = data_word["time"]

            word = game.words[id]
            word.time += time
            if status == "USED":
                word.status = Word.STATUS_USED
            elif status == "GUESSED":
                word.status = Word.STATUS_GUESSED
                game.players[first_player].explained += 1
                game.players[second_player].guessed += 1
                game.finished_words_ids.append(id)
            elif status == "FAILED":
                word.status = Word.STATUS_FAILED
                game.finished_words_ids.append(id)

        game.phase += 1
        return make_response()

api.add_resource(DoPhase, "/do_phase/<string:game_id>")


if __name__ == "__main__":
    port = 5000
    if "PORT" in os.environ:
        port = int(os.environ["PORT"])
    app.run(debug=True, port=port)
