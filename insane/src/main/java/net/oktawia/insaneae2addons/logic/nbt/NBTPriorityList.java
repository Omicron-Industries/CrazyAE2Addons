package net.oktawia.insaneae2addons.logic.nbt;

import java.util.Collections;

import appeng.api.config.IncludeExclude;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.util.prioritylist.IPartitionList;

public class NBTPriorityList implements IPartitionList {

    private final String criteria;
    private final NBTMatcher.Compiled compiled;

    public NBTPriorityList(String criteria) {
        this.criteria = criteria == null ? "" : criteria;
        this.compiled = NBTMatcher.compile(this.criteria);
    }

    @Override
    public boolean isListed(AEKey key) {
        return matchesFilter(key, IncludeExclude.WHITELIST);
    }

    @Override
    public boolean isEmpty() {
        return criteria.isEmpty();
    }

    @Override
    public Iterable<AEKey> getItems() {
        return Collections.emptyList();
    }

    @Override
    public boolean matchesFilter(AEKey key, IncludeExclude mode) {
        if (key instanceof AEItemKey ik) {
            return NBTMatcher.doesItemMatch(ik, compiled);
        }
        return false;
    }
}
