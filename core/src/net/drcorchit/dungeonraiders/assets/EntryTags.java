package net.drcorchit.dungeonraiders.assets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntryTags {

	public final ImmutableMap<String, EntryTag> tags;

	public EntryTags(JsonObject info) {
		ImmutableMap.Builder<String, EntryTag> builder = ImmutableMap.builder();

		for (Map.Entry<String, JsonElement> entry : info.entrySet()) {
			String name = entry.getKey();
			builder.put(name, new EntryTag(name, entry.getValue()));
		}

		tags = builder.build();
	}

	public static class EntryTag {
		public final String name;
		public final ImmutableSet<String> targets;

		public EntryTag(String name, JsonElement targets) {
			this.name = name;
			if (targets.isJsonArray()) {
				this.targets = ImmutableSet.copyOf(Iterators.transform(
						targets.getAsJsonArray().iterator(),
						JsonElement::getAsString));
			} else {
				this.targets = ImmutableSet.of(targets.getAsString());
			}
		}

		public Set<EntryTag> getMatchingTags(EntryTags oppositeTags) {
			return targets.stream().map(str -> oppositeTags.tags.get(name)).collect(Collectors.toSet());
		}
	}
}
