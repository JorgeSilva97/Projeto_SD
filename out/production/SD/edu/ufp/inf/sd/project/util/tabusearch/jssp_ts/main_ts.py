from bidir import BidirProblem
from tabu import TabuProblem
import time
import sys


def getInstances(name):
	""" Returns the jobshop instances from a file with given name """
	
	def getMP(M):
		tail = []
		for row in [x.split(" ") for x in M]:
			tail.append([int(x.strip()) for x in row if x]) 
		mac = []
		for row in tail:
			mac.append(row[::2])
		proc = []
		for row in tail:
			proc.append(row[1::2])

		return mac, proc
	
	#name += '.txt'
	with open(name, 'r') as f:
		l = f.read()
	l = l.split("\n")
	while l:
		i = l.pop(0)
		list = i.split(" ")
		if len(list)>1:
			global optimum
			optimum = list[2]

		if not l: continue
		nBMac = int([x for x in i.split(" ") if x][0])
		yield getMP(l[:nBMac])
		l = l[nBMac:]


def mainBidir(begin=0, end=40, it=5, c=3, inst='law'):
	'''if inst == 'law':
		data = law
		opt = opt_law
	elif inst == 'yam':
		data = yam
		opt = opt_yam
	else:
		raise ValueError('unknown instance')
	'''
	data = inst
	avg_Z = []
	
	for i,inst in enumerate(data[begin:end]):
		solution_best = float('inf')
		mac, proc = inst
		x = BidirProblem(mac, proc)
		m = x.NbMachines()
		n = len(mac)
		dt = []
		t1 = time.time()

		for r in range(it):
			x = BidirProblem(mac, proc, c=c)
			x.bidir()
			solution = x.get_cost()
			if solution < solution_best:
				solution_best = solution
				#if int(solution_best) == int(optimum):
				#	break
		t2 = time.time()
		dt.append(t2-t1)
		dt = sum(dt)/len(dt)
		#print("[TS] Makespan: %d (opt: %s), t=%f" %(solution_best, optimum, dt))
		print(solution_best)

	return avg_Z, dt


def mainTabu(begin=0, end=40, it=5, c=3):
	avg_Z = []
	for i, inst in enumerate(law[begin:end]):
		solution_best = float('inf')
		mac, proc = inst
		dt = []
		x = BidirProblem(mac, proc)
		m = x.NbMachines()
		n = len(mac)
		bsols = []
		for r in range(it*5):
			x = BidirProblem(mac, proc, c=c)
			x.bidir()
			bsols.append((x.get_cost(),x.E2))
		es = sorted(bsols, key=lambda x:x[0])
		for r in range(it):
			x = TabuProblem(mac, proc, es[r][1])
			t1 = time.time()
			x.TB()
			t2 = time.time()
			dt.append(t2-t1)
			solution = x.get_cost()
			if solution<solution_best:
				solution_best = solution
			if int(solution_best) == int(optimum):
				break
		#optimum = opt_law[i+begin]
		Delta = (solution_best-optimum)/optimum*100
		avg_Z.append(Delta)
		dt = sum(dt)/len(dt)
		#print("[TS] Makespan: %d (opt: %s), t=%f" % (i + begin + 1, solution_best, optimum, dt))
		print(solution_best)

	return avg_Z


# absolut minima for lawrence instances
#opt_law = [666, 655, 597, 590, 593, 926, 890, 863, 951, 958, 1222, 1039, 1150, 1292, 1207, 945, 784, 848, 842, 902, 1046, 927, 1032, 935, 977, 1218, 1235, 1216, 1152, 1355, 1784, 1850, 1719, 1721, 1888, 1268, 1397, 1196, 1233, 1233]
opt_law = None
# yamada optima aren't the proven optimal, but these are the best found in the paper
#opt_yam = [967, 945, 951, 1052]
#law = tuple(getInstances('data/lawrence'))
law = None
#law = tuple(getInstances('../data/abz5'))
#yam = tuple(getInstances('data/yamada'))
optimum = None


def main_ts(input_file, iterations=10):
	instance = tuple(getInstances(input_file))
	mainBidir(inst=instance, it=iterations)


def main(argv):

	bad_usage = False
	len_args = len(argv)

	if len_args < 1:
		bad_usage = True
	else:
		input_file = argv[0]
		iterations = int(argv[1]) if len_args > 1 else None
		if not iterations:
			main_ts(input_file)
		else:
			main_ts(input_file, iterations)

	if bad_usage:
		print('Usage: main_ts.py <jssp_instance_path> [iterations]')
		exit()


if __name__ == '__main__':
	main(sys.argv[1:])
