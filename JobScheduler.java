package hw2;

import java.io.*;
import java.util.*;

/*
ZEYNEP CINDEMIR - 201401012 - HW2
*/

public class JobScheduler {
    public MinHeap<Job> schedulerTree;
    public Integer timer = 0;
    public String filePath;
    public HashMap<Integer, ArrayList<Integer>> dependencyMap; // you can change Hashmap as Hashmap<Integer,Integer> or any other type
    public ArrayList<Resource> resources;
    public ArrayList<Job> completedJobs; //all completed jobs
    public ArrayList<Job> dependencyBlockedJobs; //temporary dependencyBlocked job list
    public ArrayList<Job> resourceBlockedJobs; //temporary resourceBlocked job list
    public ArrayList<Job> allJobs;
    public StringBuilder allTimeLine;

    public JobScheduler(String filePath) {
        this.filePath = filePath;
        this.dependencyMap = new HashMap<>();
        this.schedulerTree = new MinHeap<>();
        this.resources = new ArrayList<>();
        this.completedJobs = new ArrayList<>();
        this.dependencyBlockedJobs = new ArrayList<>();
        this.resourceBlockedJobs = new ArrayList<>();
        this.allTimeLine = new StringBuilder();
        this.allJobs = new ArrayList<>();
        allTimeLine.append("alltimeline\n\t R1  R2\n");
    }

    public void insertDependencies(String dependencyPath) {
        try (Scanner sc = new Scanner(new File(dependencyPath))) {
            String line;
            String[] lineSeparated;

            while (sc.hasNextLine()) {
                line = sc.nextLine();
                lineSeparated = line.split(" ");
                ArrayList<Integer> temp = new ArrayList<>();
                dependencyMap.putIfAbsent(Integer.parseInt(lineSeparated[0]), temp);
                dependencyMap.get(Integer.parseInt(lineSeparated[0])).add(Integer.parseInt(lineSeparated[1]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean stillContinues() {
        try (Scanner sc = new Scanner(new File(filePath))) {
            int a = 0;
            while (sc.hasNextLine()) {
                sc.nextLine();
                a++;
            }
            return timer < a;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void completeCheck() {
        //if completed, add to finishedJobs list of the current resource & completedJobs
        for (Resource r : resources) {
            if (!r.jobs.isEmpty()) {
                for (int i = 0; i < r.jobs.size(); i++) {
                    Job current = r.jobs.get(i);

                    if (current.isCompleted()) {
                        r.finishedJobs.add(current);
                        r.jobs.remove(0);
                        if (!completedJobs.contains(current)) {
                            completedJobs.add(current);
                            completedJobs.trimToSize();
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Integer> getDependency(Job currentJob) {
        ArrayList<Integer> text = new ArrayList<>();
        ArrayList<Integer> entry = dependencyMap.get(currentJob.getJobID());

        if (entry != null)
            for (Integer j : entry)
                if (!completedContains(j))
                    text.add(j);

        return text;
    }

    private boolean hasDependency(Job currentJob) {
        ArrayList<Integer> entry = dependencyMap.get(currentJob.getJobID());

        if (entry != null)
            for (Integer j : entry)
                if (!completedContains(j))
                    return true;

        return false;
    }

    private boolean completedContains(int j) {
        for (Job k : completedJobs)
            if (k.getJobID() == j)
                return true;
        return false;
    }

    private boolean hasEmptyResource() {
        for (Resource r : resources)
            if (r.jobs.isEmpty())
                return true;

        return false;
    }

    private void sendToResource(Job j) {
        for (Resource r : resources)
            if (r.isAvailable()) {
                r.jobs.add(j);
                break;
            }

    }

    private boolean allResourcesAreEmpty() {
        boolean checkFlag = false;

        for (Resource currRes : resources)
            if (!currRes.jobs.isEmpty()) {
                checkFlag = true;
                break;
            }

        return !checkFlag;
    }

    public void run() {
        dependencyBlockedJobs.clear();
        resourceBlockedJobs.clear();

        completeCheck();

        Job currentJob;

        while (schedulerTree.size != 0) {
            currentJob = schedulerTree.poll();
            if (!hasDependency(currentJob)) {
                if (hasEmptyResource()) {
                    currentJob.setArrival(timer);
                    sendToResource(currentJob);
                } else {
                    resourceBlockedJobs.add(currentJob);
                }
            } else {
                dependencyBlockedJobs.add(currentJob);
            }

        }

        for (Job j : resourceBlockedJobs)
            schedulerTree.add(j);
        for (Job j : dependencyBlockedJobs) {
            schedulerTree.add(j);
        }

        if (!allResourcesAreEmpty()) {
            Formatter f = new Formatter();
            f.format("%-5s", timer);

            allTimeLine.append(f);
            for (Resource r : resources) {
                if (r.getJobs().size() != 0)
                    allTimeLine.append(r.getJobs().get(0)).append("   ");
                else
                    allTimeLine.append("   ");
            }
            allTimeLine.append("\n");
        }
    }

    public void setResourcesCount(Integer count) {
        for (int i = 1; i <= count; i++) {
            Resource resource = new Resource(i);
            resources.add(resource);
        }
    }

    private void readJobs() {
        Scanner sc;
        allJobs.clear();
        try {
            sc = new Scanner(new File(filePath));
            String line;
            String[] jobInfo;

            while (sc.hasNextLine()) {
                line = sc.nextLine();
                jobInfo = line.split(" ");
                if (!line.equals("no job"))
                    allJobs.add(new Job(Integer.parseInt(jobInfo[0]), Integer.parseInt(jobInfo[1]), 0));
                else {
                    allJobs.add(null);
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertJob() {
        timer++;
        readJobs();
        if (allJobs.get(timer - 1) != null)
            schedulerTree.add(allJobs.get(timer - 1));
    }

    public void completedJobs() {
        System.out.print("completed jobs ");

        for (int i = 0; i < completedJobs.size(); i++) {
            if (i == completedJobs.size() - 1)
                System.out.print(completedJobs.get(i));
            else
                System.out.print(completedJobs.get(i) + ", ");
        }

        System.out.println();
    }

    public void dependencyBlockedJobs() {
        System.out.print("dependency blocked jobs ");
        //print dependencyBlockedJobs's elements
        for (Job currJ : dependencyBlockedJobs) {
            ArrayList<Integer> depList = getDependency(currJ);

            if (depList != null)
                for (Integer currDep : depList)
                    System.out.print("(" + currJ + "," + currDep + ")" + " ");
        }

        System.out.println();
    }

    public void resourceBlockedJobs() {
        System.out.print("resource blocked jobs ");
        //print resourceBlockedJobs's elements
        for (int i = 0; i < resourceBlockedJobs.size(); i++) {
            if (i == resourceBlockedJobs.size() - 1)
                System.out.print(resourceBlockedJobs.get(i));
            else
                System.out.print(resourceBlockedJobs.get(i) + ", ");
        }

        System.out.println();
    }

    public void workingJobs() {
        System.out.print("working jobs ");
        for (Resource r : resources)
            for (Job j : r.jobs)
                System.out.print("(" + j + "," + r.num + ")" + " ");

        System.out.println();
    }

    public void runAllRemaining() {
        boolean runFlag = !allResourcesAreEmpty();

        while (runFlag) {
            timer++;
            run();
            runFlag = !allResourcesAreEmpty();
        }
    }

    public void allTimeLine() {
        System.out.println(allTimeLine);
    }

    public String toString() {
        return schedulerTree.toString();
    }

    public static class MinHeap<T extends Comparable<T>> {
        T[] items;
        private int capacity;
        private int size;

        public MinHeap() {
            size = 0;
            capacity = 10;
            items = (T[]) new Comparable[capacity];
        }

        public int getLeftChildIdx(int parentIdx) {
            return 2 * parentIdx + 1;
        }

        public int getRightChildIdx(int parentIdx) {
            return 2 * parentIdx + 2;
        }

        public int getParentIdx(int childIdx) {
            return (childIdx - 1) / 2;
        }

        public boolean hasLeftChild(int idx) {
            return getLeftChildIdx(idx) < size;
        }

        public boolean hasRightChild(int idx) {
            return getRightChildIdx(idx) < size;
        }

        public boolean hasParent(int idx) {
            return getParentIdx(idx) >= 0;
        }

        public T leftChild(int idx) {
            return items[getLeftChildIdx(idx)];
        }

        public T rightChild(int idx) {
            return items[getRightChildIdx(idx)];
        }

        public T parent(int idx) {
            return items[getParentIdx(idx)];
        }

        public void swap(int idx1, int idx2) {
            T tmp = items[idx1];
            items[idx1] = items[idx2];
            items[idx2] = tmp;
        }

        public void extraCapacity() {
            if (size == capacity) {
                items = Arrays.copyOf(items, capacity * 2);
                capacity *= 2;
            }
        }

        public T peek() {
            if (size == 0) throw new IllegalStateException();
            return items[0];
        }

        public T poll() {
            if (size == 0) throw new IllegalStateException();
            T item = items[0];
            items[0] = items[size - 1];
            size--;
            heapifyDown();
            return item;
        }

        public void add(T item) {
            extraCapacity();
            items[size++] = item;
            heapifyUp();
        }

        public void heapifyUp() {
            int idx = size - 1;
            while (hasParent(idx) && parent(idx).compareTo(items[idx]) > 0) {
                swap(getParentIdx(idx), idx);
                idx = getParentIdx(idx);
            }
        }

        public void heapifyDown() {
            int idx = 0;
            while (hasLeftChild(idx)) {
                int smallerChildIdx = getLeftChildIdx(idx);
                if (hasRightChild(idx) && rightChild(idx).compareTo(leftChild(idx)) < 0) {
                    smallerChildIdx = getRightChildIdx(idx);
                }
                if (items[idx].compareTo(items[smallerChildIdx]) < 0) {
                    break;
                } else {
                    swap(idx, smallerChildIdx);
                }
                idx = smallerChildIdx;
            }
        }

        @Override
        public String toString() {
            StringBuilder heapTree = new StringBuilder();
            int height = (int) Math.ceil(Math.log(size + 1) / Math.log(2)) - 1;

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < Math.pow(2, i) && j + Math.pow(2, i) < capacity; j++) {
                    if (items[j + (int) Math.pow(2, i) - 1] != null && j + (int) Math.pow(2, i) - 1 < size) {
                        heapTree.append("\t".repeat(Math.max(0, height / ((int) Math.pow(2, i)))));
                        heapTree.append(items[j + (int) Math.pow(2, i) - 1]).append("\t\t");
                    }
                }

                if (Math.floor(Math.log(i + 1) / Math.log(2)) < Math.floor(Math.log(size) / Math.log(2)))
                    heapTree.append("\n");
            }
            /* [ALTERNATIVE METHOD]
            int nBlanks = 8;
            int jobsPerRow = 1;
            int columnNo = 0;
            int currJ = 0;                                  // CURRENT JOB NO

            while (size > 0)                                // FOR LOOP EACH JOB
            {
                if (columnNo == 0)                          // FIRST JOB IN ROW?
                    for (int k = 0; k < nBlanks; k++)       // PRINT BLANKS
                        heapTree.append(' ');
                // display item
                heapTree.append(items[currJ]);

                if (++currJ == size)                    // IS FINISH?
                    break;

                if (++columnNo == jobsPerRow)           // IS END OF ROW?
                {
                    nBlanks /= 2;                       // HALF BLANKS
                    jobsPerRow *= 2;                    // DOUBLE JOB NUMBER
                    columnNo = 0;                       // RESET COLUMN
                    heapTree.append("\n");              // NEW ROW
                } else                                  // NEST JOB ON ROW
                    for (int k = 0; k < nBlanks * 2 - 2; k++)
                        heapTree.append(' ');           // PRINT BLANKS
            }
			*/
            return heapTree.toString();
        }
    }

    public class Job implements Comparable<Job> {
        private int jobID;
        private int duration;
        private int arrival;

        public Job() {
            this.jobID = 0;
            this.duration = 0;
        }

        public Job(int jobID, int duration, int arrival) {
            this.jobID = jobID;
            this.duration = duration;
            this.arrival = arrival;
        }

        public boolean isCompleted() {
            return (arrival + duration == timer);
        }

        public int getJobID() {
            return jobID;
        }

        public void setJobID(int jobID) {
            this.jobID = jobID;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getArrival() {
            return arrival;
        }

        public void setArrival(int arrival) {
            this.arrival = arrival;
        }


        @Override
        public int compareTo(Job o) {
            if (o == null) {
                throw new ClassCastException();
            } else return Integer.compare(this.arrival, o.arrival);
        }

        @Override
        public String toString() {
            return Integer.toString(jobID);
        }
    }

    public class Resource {
        private final int num;
        private ArrayList<Job> jobs;
        private ArrayList<Job> finishedJobs;

        public Resource(int num) {
            this.num = num;
            jobs = new ArrayList<>();
            finishedJobs = new ArrayList<>();
        }

        public boolean isAvailable() {
            return this.jobs.isEmpty();
        }

        @Override
        public String toString() {
            return "R" + num;
        }

        public ArrayList<Job> getJobs() {
            return jobs;
        }

        public int getNum() {
            return num;
        }
    }
}