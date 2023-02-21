package hw2;

public class Surucu {
    public static void main(String[] args) {
        String path1 = "C:\\Users\\zcind\\IdeaProjects\\bil212\\src\\hw2\\Jobs.txt";
        String path2 = "C:\\Users\\zcind\\IdeaProjects\\bil212\\src\\hw2\\Dependencies.txt";
        JobScheduler cizelge = new JobScheduler(path1);
        cizelge.setResourcesCount(2); //same as one in hw1
        cizelge.insertDependencies(path2);
//        System.out.println(cizelge.dependencyMap);

        while(cizelge.stillContinues()){ // stillContinues metodu ile job dosyasında bir şey bulunup bulunmadığına bakılır
            cizelge.insertJob(); //insertJob reads a new line from the inputfile, adds a job if necessary
            System.out.println("min-heap\n" + cizelge); // a proper toString method for JobSchedular class
            cizelge.run(); //different from one in hw1
            //printing as a list and printing as a tree
            cizelge.completedJobs(); // prints completed jobs and their completion time
            cizelge.dependencyBlockedJobs(); // prints jobs whose time is up but waits due to its dependency, also prints its dependency
            cizelge.resourceBlockedJobs(); // prints jobs whose time is up but waits due to busy resources
            cizelge.workingJobs(); // prints jobs working on this cycle and its resource
            System.out.println("-------------"+cizelge.timer+"-------------");
        }

        cizelge.runAllRemaining();
        cizelge.allTimeLine();
    }
}

/*
min-heap
1
completed jobs
dependency blocked jobs
resource blocked jobs
working jobs (1,1)
------------- 1 -------------
min-heap
2
completed jobs
dependency blocked jobs
resource blocked jobs
working jobs (1,1) (2,2)
------------- 2 -------------
min-heap
completed jobs
dependency blocked jobs
resource blocked jobs
working jobs (1,1) (2,2)
------------- 3 -------------
min-heap
3
completed jobs 1, 2
dependency blocked jobs
resource blocked jobs
working jobs (3,1)
------------- 4 -------------
min-heap
4
completed jobs 1, 2
dependency blocked jobs (4,3)
resource blocked jobs
working jobs (3,1)
------------- 5 -------------
min-heap
4
completed jobs 1, 2
dependency blocked jobs (4,3)
resource blocked jobs
working jobs (3,1)
------------- 6 -------------
min-heap
    4
5
completed jobs 1,2
dependency blocked jobs (4,3) (5,3)
resource blocked jobs
working jobs (3,1)
------------- 7 -------------
min-heap
	4
5		6
completed jobs 1, 2, 3
dependency blocked jobs
resource blocked jobs 6
working jobs (4,1) (5,2)
------------- 8 -------------
min-heap
	6
7
completed jobs 1, 2, 3, 4
dependency blocked jobs (7,5)
resource blocked jobs
working jobs (6,1) (5,2)
------------- 9 -------------
alltimeline
	R1	R2
1	1
2	1	2
3	1	2
4	3
5	3
6	3
7	3
8	4	5
9	6	5
10	6	7
11	6	7

 */