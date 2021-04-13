import logging
import random
from enum import Enum, unique

from utils import swap_rnd, notify_rmq


def removeFromList(parent, list):
    seen = set()
    seen_add = seen.add
    return [x for x in parent if not (x in list or seen_add(x))]


def replaceWithRandomPopulation(population, q, n, mn):
    for i in range(q):
        population.pop()
    for i in range(q):
        addRandomIndividual(population, n, mn)


def checkDiversity(population, diff, n, mn):
    if diff < 0.03:
        replaceWithRandomPopulation(population, int(n/3), n, mn)
    if diff < 0.05:
        replaceWithRandomPopulation(population, int(n/5), n, mn)
    elif diff < 0.1:
        replaceWithRandomPopulation(population, int(n/10), n, mn)


def getFitness(population):
    prev = population[0][1]
    total = 0
    diffPercentage = 0.0
    for ind in population:
        curr = ind[1]
        total += curr
        diffPercentage += (curr/float(prev)) - 1
        prev = curr

    return total, diffPercentage


#Each individual is compose by a permutation(list from 0 to the job_number*machine_number)
#And a second parameter that is filled with the result of the makespan for the permutation
#We keep track of the result to not calculate multiple times the same result unnecesarily
#Is important to remove that number every time the permutation change
def addRandomIndividual(population, n, mn):
    ind = list(range(n*mn))
    random.shuffle(ind)
    population.append([ind, None])


#We generate the number of population
def generate_population(number, n, mn):
        population = []
        for i in range(number):
            addRandomIndividual(population, n, mn)
        return population


#During the crossover we select gens from the father from the start to the end index defined, we remove those from the mother
#Then we add them to the resultant in the same order that it was in the father origininally
def crossover(father, mother, start_index, end_index):
    father_gen = father[0][start_index:end_index]
    fetus = removeFromList(mother[0], father_gen)
    result = []
    result.extend(fetus[:start_index])
    result.extend(father_gen)
    result.extend(fetus[start_index:])
    return [result, None]


#mutate one member of the poupulation randomly excluding the first one(best individual)
#We just change the order of the permutation by one
def mutation(population, mutation_rate):
    if(random.random() > mutation_rate):
        candidate = random.choice(population[1:])
        swap_rnd(candidate[0])
        candidate[1] = None


def evolve(population, mutation_rate):
    # Important: the population should be sorted before evolve

    global strategy

    # Get the parents from the population
    father = random.choice(population)
    mother = random.choice(population)
    while mother == father:
        mother = random.choice(population)

    # Select which part of the father will go to the mother
    indexes = range(len(father[0]))
    start_index = random.choice(indexes)
    end_index = random.choice(indexes[start_index:])

    if strategy == CrossoverStrategies.ONE:
        # Two parents generate two children that will substitute them in the population

        # Generate the children with the crossover
        child1 = crossover(father, mother, start_index, end_index)
        child2 = crossover(mother, father, start_index, end_index)

        # Delete the parents from the population
        population.remove(father)
        population.remove(mother)

        # Add the new members to the population
        population.append(child1)
        population.append(child2)

    elif strategy == CrossoverStrategies.TWO:
        # Two parents generate two children that will substitute two random individuals in the population.
        #   The most fit individual lives (elitism)

        # Generate the children with the crossover
        child1 = crossover(father, mother, start_index, end_index)
        child2 = crossover(mother, father, start_index, end_index)

        # Delete two random individuals from the population (except the most fit)
        indexes = range(len(population[1:]))
        indiv1 = random.choice(indexes)
        indiv2 = random.choice(indexes)
        while indiv1 == indiv2:
            indiv2 = random.choice(indexes)
        population.pop(indiv1)
        population.pop(indiv2)

        # we add the new members to the population
        population.append(child1)
        population.append(child2)

    elif strategy == CrossoverStrategies.THREE:
        # Two parents generate a child that will substitute the worst individual of the population

        # Delete the worst individual of the population
        population.pop()

        # Generate the baby with the crossover
        child = crossover(father, mother, start_index, end_index)

        # Add the new member to the population
        population.append(child)

    # Trigger the mutation for one of the population, depending on the mutation rate
    mutation(population, mutation_rate)

    return population


@unique
class CrossoverStrategies(Enum):
    ONE = 1
    TWO = 2
    THREE = 3


strategy = CrossoverStrategies.ONE


def set_strategy(s):
    global strategy
    try:
        log_message = "Setting Strategy {0}".format(s)
        print(log_message)
        # logger.info(log_message)
        notify_rmq(log_message)
        strategy = CrossoverStrategies(int(s))
    except ValueError:
        log_message = "{0} is not a valid Strategy. Going to use Strategy 1.".format(s)
        print(log_message)
        # logger.info(log_message)
        notify_rmq(log_message)
        set_strategy(1)


'''logger = None


def set_logger():
    global logger
    logger = logging.getLogger('jssp_ga')
    if logger.hasHandlers():
        logger.handlers.clear()
    logger.setLevel(logging.INFO)
    fh = logging.FileHandler('jssp_ga.log', mode='w')
    fh.setLevel(logging.INFO)
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(message)s')
    fh.setFormatter(formatter)
    logger.addHandler(fh)
    return logger
'''
