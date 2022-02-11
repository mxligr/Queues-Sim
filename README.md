# Queues-Sim
Simulation Application aiming to analyse queuing based systems for determining and minimizing clients’ waiting time, developed for the Fundamental Programming Techniques course // 2nd year, 2nd semester @ Computer Science, TUCN

The application implemented in Java simulates (by defining a simulation time 𝑡𝑠𝑖𝑚𝑢𝑙𝑎𝑡𝑖𝑜𝑛) a series of N clients (generated randomly) arriving for service, entering Q queues, waiting, being served and finally leaving 
the queues, with the help of multithreading and ensuring thread safety. All clients are generated when the simulation is started. The application tracks the total time spent by every client in the queues 
and computes the average waiting time. Each client is added to the queue with minimum waiting time when its 𝑡𝑎𝑟𝑟𝑖𝑣𝑎𝑙 time is greater than or equal to the simulation time.<br>
A number of Q threads are launched to process in parallel the clients. Another thread is launched to hold the simulation time 𝑡𝑠𝑖𝑚𝑢𝑙𝑎𝑡𝑖𝑜𝑛 and distribute each client i to the queue 
with the smallest waiting time. <br>
The graphical user interface is used for setting up the simulation (introducing the necessary parameters by the user). The result of the simulation is saved in a .txt file corresponding to the log of events.
