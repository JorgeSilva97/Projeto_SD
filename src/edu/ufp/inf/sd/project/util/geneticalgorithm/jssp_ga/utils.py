import os
import random

import pika

from calculateMakespan import calculateMakespan
import GAOperations


def readFilePairs(filepath):
    times_done = False
    times = []
    machines = []

    with open(filepath) as fp:
        line = fp.readline()
        n, mn, optimum = line.strip().split(' ')
        line = fp.readline()

        while line:
            parse_line = ' '.join(line.split())
            raw_line = parse_line.strip().split(' ')
            curr = []
            i = 0
            machine = []
            time = []
            while i < len(raw_line):
                m, t = raw_line[i], raw_line[i + 1]
                machine.append(int(m))
                time.append(int(t))
                i += 2

            times.append(time)
            machines.append(machine)
            line = fp.readline()

    return times, machines, int(n), int(optimum)

def readSolution(filepath):
    machines = []

    with open(filepath) as fp:

        line = fp.readline()
        while line:
            raw_line = line.strip().split(' ')
            curr = []
            for char in raw_line:
                if len(char) > 0:
                    curr.append(int(char))
            machines.append(curr)
            line = fp.readline()
    sequence = []
    for i in range(len(machines[0])):
        for j in range(len(machines)):
            sequence.append(machines[j][i])
    return sequence

def swap_rnd(config):
    id1 = random.choice(range(len(config)))
    id2 = random.choice(range(len(config)))
    tmp = config[id1]
    config[id1] = config[id2]
    config[id2] = tmp
    return config

def fromPermutation(permutation, n):
    return list(map(lambda  x: x%n, permutation))
    

def testPermutation(permutation, times, machines, n):
    best_result, table = calculateMakespan(times, machines, permutation, n)
    print("SEQUENCE")
    print(permutation)
    print("RESULT:")
    print(best_result)
    job_sequence = []
    print("TABLE:")
    i = 1
    for row in table:
        print("M%s: %s" %(i, row))
        for slot in row:
            job_sequence.append(slot[2])
        i += 1
    print(job_sequence)

def printTable(table):
    i = 1
    print("TABLE: ")
    for row in table:
        print("M%s: %s" %(i, row))
        i += 1


queue = None


def set_queue(q):
    global queue
    queue = q


def notify_rmq(message):
    global queue

    if queue:
        queue_results = queue + "_results"

        connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        channel = connection.channel()

        channel.queue_declare(queue=queue_results)

        channel.basic_publish(exchange='', routing_key=queue_results, body=str(message))
        connection.close()


def listen_rmq():
    global queue

    if queue:
        connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        channel = connection.channel()

        channel.queue_declare(queue=queue)

        def callback(ch, method, properties, body):
            message = body.decode()
            log_message = "Received message = '%s'" % message
            print(log_message)
            # logger.info(log_message)
            if message == "stop":
                print("Stopping...")
                # logger.info("Stopping...")
                notify_rmq("Stopping...")
                os._exit(0)
            else:
                GAOperations.set_strategy(message)

        channel.basic_consume(queue=queue, on_message_callback=callback, auto_ack=True)

        # print(' [*] Waiting for messages. To exit press CTRL+C')
        channel.start_consuming()
