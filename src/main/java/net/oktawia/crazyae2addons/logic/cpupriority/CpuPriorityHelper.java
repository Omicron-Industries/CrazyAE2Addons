package net.oktawia.crazyae2addons.logic.cpupriority;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.experimental.UtilityClass;

import appeng.api.networking.crafting.ICraftingCPU;
import appeng.me.cluster.implementations.CraftingCPUCluster;

import net.oktawia.crazyae2addons.logic.interfaces.ICpuListEntryInfo;
import net.oktawia.crazyae2addons.logic.interfaces.ICpuListView;
import net.oktawia.crazyae2addons.logic.interfaces.ICpuPrio;

@UtilityClass
public class CpuPriorityHelper {

    public int getCpuPriority(ICraftingCPU cpu) {
        if (cpu instanceof ICpuPrio prio) {
            return prio.getPrio();
        }
        return 0;
    }

    public int getClusterPriority(CraftingCPUCluster cluster) {
        if ((Object) cluster instanceof ICpuPrio prio) {
            return prio.getPrio();
        }
        return 0;
    }

    public int getEntryPriority(Object entry) {
        if (entry instanceof ICpuPrio prio) {
            return prio.getPrio();
        }
        return 0;
    }

    public int getEntrySerial(Object entry) {
        if (entry instanceof ICpuListEntryInfo info) {
            return info.serial();
        }
        return 0;
    }

    public String getEntryName(Object entry) {
        if (entry instanceof ICpuListEntryInfo info) {
            var name = info.name();
            return name == null ? "CPU" : name.getString();
        }
        return "CPU";
    }

    public void applyEntryPriorities(Object cpuList, Set<ICraftingCPU> cpus, Map<ICraftingCPU, Integer> serials) {
        if (!(cpuList instanceof ICpuListView list) || cpus == null) {
            return;
        }

        var entries = list.cpus();
        if (entries.isEmpty()) {
            return;
        }

        var serialToCpu = new HashMap<Integer, ICraftingCPU>(serials.size());
        for (var cpu : cpus) {
            var serial = serials.get(cpu);
            if (serial != null) {
                serialToCpu.put(serial, cpu);
            }
        }

        for (var entry : entries) {
            if (entry instanceof ICpuPrio prio) {
                prio.setPrio(getCpuPriority(serialToCpu.get(getEntrySerial(entry))));
            }
        }
    }

    public Comparator<ICraftingCPU> cpuComparator() {
        return Comparator
                .comparingInt(CpuPriorityHelper::getCpuPriority)
                .reversed()
                .thenComparingInt(System::identityHashCode);
    }

    public Comparator<CraftingCPUCluster> clusterComparator() {
        return Comparator
                .comparingInt(CpuPriorityHelper::getClusterPriority)
                .reversed()
                .thenComparingInt(System::identityHashCode);
    }

    public Comparator<CraftingCPUCluster> clusterComparatorAscending() {
        return Comparator
                .comparingInt(CpuPriorityHelper::getClusterPriority)
                .thenComparingInt(System::identityHashCode);
    }

    public Comparator<Object> entryComparator() {
        return Comparator
                .comparingInt(CpuPriorityHelper::getEntryPriority)
                .reversed()
                .thenComparing(CpuPriorityHelper::getEntryName, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(CpuPriorityHelper::getEntrySerial);
    }

    public Comparator<CraftingCPUCluster> extendFastFirstComparator(Comparator<CraftingCPUCluster> base) {
        return Comparator
                .comparingInt(CpuPriorityHelper::getClusterPriority)
                .reversed()
                .thenComparing(base)
                .thenComparingInt(System::identityHashCode);
    }

    public Comparator<CraftingCPUCluster> extendFastLastComparator(Comparator<CraftingCPUCluster> base) {
        return Comparator
                .comparingInt(CpuPriorityHelper::getClusterPriority)
                .thenComparing(base)
                .thenComparingInt(System::identityHashCode);
    }

    public List<ICraftingCPU> sortCpus(Collection<? extends ICraftingCPU> cpus) {
        return cpus.stream()
                .sorted(cpuComparator())
                .map(cpu -> (ICraftingCPU) cpu)
                .toList();
    }

    public List<CraftingCPUCluster> sortClusters(Collection<CraftingCPUCluster> clusters) {
        return clusters.stream()
                .sorted(clusterComparator())
                .toList();
    }

    public Iterator<CraftingCPUCluster> sortedClusterIterator(Collection<CraftingCPUCluster> clusters) {
        return sortClusters(clusters).iterator();
    }

    public <T> List<T> sortEntries(List<T> entries) {
        return entries.stream()
                .sorted(entryComparator())
                .toList();
    }
}
