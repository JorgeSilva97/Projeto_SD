import copy
import os
import threading
import time
import sys

from calculateMakespan import calculateMakespan
from GAOperations import checkDiversity, generate_population, getFitness, evolve, set_strategy
from utils import readFilePairs, notify_rmq, listen_rmq, set_queue


def print_best(best_result, optimum, start_time):
	# print("[GA] Makespan: %s (opt: %s), t=%f" %(best_result,optimum,time.time() - start_time))
	print(best_result)
	# logger.info(best_result)
	notify_rmq("Makespan = {0}".format(best_result))


def printProgress(bestValue, iterations, timeElapsed):
	sys.stdout.write("\rIterations: {0} | Best result found {1} | Time elapsed: {2}s".format(iterations, bestValue,
																							 int(timeElapsed)))
	sys.stdout.flush()


def genetic(times, machines, n, population_number, iterations, rate, target, optimum):
	machine_number = len(machines[0])
	start_time = time.time()

	def sortAndGetBestIndividual(population):
		best_individual = None
		best_result = None
		for individual in population:
			result = None
			if not individual[1]:
				result, table = calculateMakespan(times, machines, individual[0], n)
				individual[1] = result
			else:
				result = individual[1]

			if not best_result or result < best_result:
				best_result = result
				best_individual = individual

		population.sort(key=lambda x: x[1])
		return best_individual, best_result

	population = generate_population(population_number, n, machine_number)
	global_best_ind, global_best = sortAndGetBestIndividual(population)

	# if we don't define a target we set the number of iterations we want
	if not target:
		for i in range(iterations):
			population = evolve(population, rate)
			best_ind, best_result = sortAndGetBestIndividual(population)
			# total_fitness, diffPercentage = getFitness(population)

			if not global_best or best_result < global_best:
				global_best = best_result
				global_best_ind = copy.deepcopy(best_ind)
				# if int(global_best) == int(optimum):
				# break
				print_best(best_result, optimum, start_time)

			# printProgress(best_result, i, time.time() - start_time)
			# checkDiversity(population, diffPercentage, n, machine_number)
	else:
		# If we define a target we iterate until the best result reach that target
		i = 0
		while (target < global_best):
			i += 1
			# in every iteration:
			# We evolve the population
			population = evolve(population, rate)
			# We find the best individual
			best_ind, best_result = sortAndGetBestIndividual(population)
			# We calculate the diversity % between the population and the total_fitness(sum of all the results)
			total_fitness, diffPercentage = getFitness(population)

			# if the result found is better than the global found we update the global
			if (not global_best or best_result < global_best):
				global_best = best_result
				global_best_ind = copy.deepcopy(best_ind)
			# We print the progress so far and the time elapsed
			printProgress(best_result, i, time.time() - start_time)
			# We check the diversity, in case the diversity percentage is very low we delete a number of the population and we add randome members
			checkDiversity(population, diffPercentage, n, machine_number)

	best_result, best_table = calculateMakespan(times, machines, global_best_ind[0], n)

	# print("[GA] Makespan: %s (opt: %s), t=%f" %(best_result,optimum,time.time() - start_time))
	# print_best(best_result, optimum, start_time)


def main_ga(input_file, generations=sys.maxsize):
	target = None
	population_size = 10
	mutation_rate = 0.01
	times, machines, n, optimum = readFilePairs(input_file)
	genetic(times, machines, n, population_size, generations, mutation_rate, target, optimum)


def main(argv):
	bad_usage = False
	len_args = len(argv)

	if len_args < 1:
		bad_usage = True
	else:
		input_file = argv[0]
		queue = argv[1] if len_args > 1 else None
		strategy = int(argv[2]) if len_args > 2 else 1
		# generations = int(argv[3]) if len_args > 2 else None
		# logger = set_logger()
		if queue:
			set_queue(queue)
			rmq = threading.Thread(target=listen_rmq)
			rmq.start()
		if strategy:
			set_strategy(strategy)
		'''if not generations:
			main_ga(input_file)
		else:
			main_ga(input_file, generations)'''
		main_ga(input_file)

	if bad_usage:
		print('Usage: main_ga.py <jssp_instance_path> [queue] [strategy]')
		exit()


if __name__ == '__main__':
	try:
		main(sys.argv[1:])
	except KeyboardInterrupt:
		print('Interrupted')
		try:
			sys.exit(0)
		except SystemExit:
			os._exit(0)
