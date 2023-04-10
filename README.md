# SpringBot

<h3>To start the bot you need:</h5>
<p>1) create property file in:  dispatcher/src/main/resources/application.properties</p>
<p>Set value for fields:</p>
<p>server.port=YOUR_VALUE</p>
<p>bot.name=YOUR_VALUE</p>
<p>bot.token=YOUR_VALUE</p>
<p>Bot name and bot token getting from BotFather in Telegram app.</p>


<p>2) Скачать образ rabbitmq:</p>
$ docker pull rabbitmq:3.11.0-management

Создать volume:
$ docker volume create rabbitmq_data

Запустить контейнер с rabbitmq:
$ docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 -v rabbitmq_data:/var/lib/rabbitmq --restart=unless-stopped rabbitmq:3.11.0-management
Флаги:
--detach , -d   запустит контейнер в фоновом режиме и вернет идентификатор контейнера в терминал;
--hostname   адрес контейнера для подключения к нему внутри docker из других контейнеров;
--name   имя контейнера;
-p    порты: первый порт — тот, по которому мы будет подключаться снаружи docker, а второй — тот, который используется внутри контейнера;
-v   примонтировать volume (том), т. е. внешнее хранилище данных;
--restart=unless-stopped   контейнер будет подниматься заново при каждом перезапуске системы (точнее, при запуске docker);

Так путь к volume может выглядеть в Windows:
rabbitmq_data:c:\rabbitmq_data

Подключиться к контейнеру с rabbitmq:
$ docker exec -it rabbitmq /bin/bash

Внутри контейнера создать пользователя, сделать его админом и установить права:
$ rabbitmqctl add_user userok p@ssw0rd
$ rabbitmqctl set_user_tags userok administrator
$ rabbitmqctl set_permissions -p / userok ".*" ".*" ".*"

dispatcher/src/main/resources/application.properties add data:
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=<>
spring.rabbitmq.password=<>