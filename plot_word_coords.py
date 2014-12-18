# -*- coding: utf-8 -*-
"""
Created on Thu Dec 18 02:49:59 2014

@author: mcs
"""
import sys, numpy
from numpy import genfromtxt
import matplotlib.pyplot as plt

coords_filename = sys.argv[1]
data = genfromtxt(coords_filename, delimiter=',', dtype=('f20,f20,a50'))
x = data['f0']
y = data['f1']
labels = data['f2']

if len(sys.argv) > 2:
    pruning_words = sys.argv[2]
    words_to_prune = {}
    with open(pruning_words, 'r') as prune_file:
        for line in prune_file:
            toks = line.split()
            if len(toks) > 1:
                raise Exception("too many tokens per line")
            words_to_prune[toks[0]] = 1
    if len(words_to_prune) > 0:
        rows_to_prune = []
        for i,label in enumerate(labels):
            if label in words_to_prune:
                rows_to_prune.append(i)
        data = numpy.delete(data, rows_to_prune)
x = data['f0']
y = data['f1']
labels = data['f2']
plt.scatter(x,y)
plt.show()