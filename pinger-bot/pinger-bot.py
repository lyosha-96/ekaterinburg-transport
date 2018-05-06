import vk
import time
import json
import logging
import threading

logging.basicConfig(filename="pinger-bot.log", level=logging.INFO, format="%(asctime)s:%(levelname)s:%(name)s:%(message)s")

notifyState = False
checkTimer = 0

def notifyDevelopers(vkapi, discussionID, notification):
    global notifyState
    logging.info("notifyDevelopers: Bot has fallen")
    print("notifyDevelopers: Bot has fallen")
    vkapi.messages.send(chat_id=66, message=notification)
    notifyState = True


def sender(vkapi, botID, waitTime=300):
    global notifyState
    while notifyState == False:
        logging.info("sender: Are you alive?")
        print("sender: Are you alive?")
        vkapi.messages.send(user_id=botID, message='Ты живой?')
        time.sleep(waitTime)

def receiver(vkapi, botID, discussionID, waitTime=300):
    global checkTimer
    global notifyState
    while notifyState == False:
        response = vkapi.messages.get(time_offset=1)
        if response['items']:
            userID = response['items'][0]['user_id']
            readState = response['items'][0]['read_state']

            print(userID, response['items'][0]['body'])
            logging.info("%s: " + response['items'][0]['body'], userID)

            if userID != int(botID) and readState == 0:
                print('messages.send to:', userID)
                logging.info("messages.send to: %s", userID)
                # vkapi.messages.send(user_id=userID, message='Здравствуйте! Я бот, я ответил потому, что меня тестируют аккаунте Айнура. Пока!')
             
            if userID == int(botID) and readState == 0:
                checkTimer = 0
                logging.info("Bot alive")
                print("Bot alive")

        elif checkTimer > (waitTime + 10):
            notifyDevelopers(vkapi, discussionID, 'Привет парни. По ходу бот упал. Посмотрите пожалуйста. Не забудьте меня перезапустить.')
            logging.info("checkTimer > waitTime : notify developers")
            print("checkTimer > waitTime : notify developers")
            
        time.sleep(1)
        checkTimer = checkTimer + 1
        print('checkTimer:', checkTimer)


def main():

    with open('.authdata.json') as json_data:
        data = json.load(json_data)
        json_data.close()
        
    appID         = data['app_id']
    userLogin     = data['user_login']
    userPassword  = data['user_password']
    bot_id        = data['bot_id']
    discussion_id = data['discussion_id']

    logging.info("Pending start...")
    
    vk_session = vk.AuthSession(app_id=appID, user_login=userLogin, user_password=userPassword, scope='wall, messages')
    vkapi = vk.API(vk_session, v='5.35', lang='ru', timeout=10)
    
    sender_thread = threading.Thread(target=sender, args=(vkapi, bot_id, ))
    receiver_thread = threading.Thread(target=receiver, args=(vkapi, bot_id, discussion_id, ))

    sender_thread.start()
    receiver_thread.start()

    sender_thread.join()
    receiver_thread.join()
    

if __name__ == '__main__':
    main()
