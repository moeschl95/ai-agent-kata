# Template Method Pattern

## Intent
Define the skeleton of an algorithm in a base class, deferring specific steps to subclasses. Subclasses can override certain steps without changing the overall algorithm structure.

## Why It Fits Here
Every Gilded Rose item update follows a common skeleton:
1. (Optionally) decrease `sellIn`.
2. Compute the quality delta based on `sellIn` and item-specific rules.
3. Clamp `quality` within bounds.

Only step 2 varies per item type. The Template Method captures steps 1 and 3 in a base class and leaves step 2 abstract.

## Implementation

```java
abstract class ItemUpdater {

    // The template method — final so subclasses can't accidentally break the skeleton
    public final void update(Item item) {
        decreaseSellIn(item);
        int delta = computeQualityDelta(item);
        applyQualityDelta(item, delta);
    }

    // Hook — override to suppress sellIn decrement (e.g., Sulfuras)
    protected void decreaseSellIn(Item item) {
        item.sellIn--;
    }

    // Abstract step — each subclass provides its own quality logic
    protected abstract int computeQualityDelta(Item item);

    // Shared clamping logic — subclasses rarely need to override this
    protected void applyQualityDelta(Item item, int delta) {
        item.quality = Math.max(0, Math.min(50, item.quality + delta));
    }
}
```

### Concrete Subclasses

```java
class NormalItemUpdater extends ItemUpdater {
    @Override
    protected int computeQualityDelta(Item item) {
        // After sellIn has already been decremented
        return item.sellIn < 0 ? -2 : -1;
    }
}

class AgedBrieUpdater extends ItemUpdater {
    @Override
    protected int computeQualityDelta(Item item) {
        return item.sellIn < 0 ? 2 : 1;
    }
}

class SulfurasUpdater extends ItemUpdater {
    @Override
    protected void decreaseSellIn(Item item) {
        // Sulfuras never ages — skip the decrement
    }

    @Override
    protected int computeQualityDelta(Item item) {
        return 0; // Quality is fixed at 80
    }

    @Override
    protected void applyQualityDelta(Item item, int delta) {
        // Do nothing — Sulfuras quality is immutable
    }
}

class BackstagePassUpdater extends ItemUpdater {
    @Override
    protected int computeQualityDelta(Item item) {
        if (item.sellIn < 0) return -item.quality; // drops to zero
        if (item.sellIn < 5)  return 3;
        if (item.sellIn < 10) return 2;
        return 1;
    }

    @Override
    protected void applyQualityDelta(Item item, int delta) {
        if (delta <= -item.quality) {
            item.quality = 0; // Special case: total loss after concert
        } else {
            super.applyQualityDelta(item, delta);
        }
    }
}

class ConjuredItemUpdater extends ItemUpdater {
    @Override
    protected int computeQualityDelta(Item item) {
        return item.sellIn < 0 ? -4 : -2;
    }
}
```

## Combining Template Method with Strategy

Template Method and Strategy are often combined:
- Use **Template Method** as the base class to share the skeleton and clamping.
- Use **Strategy** (the interface role) to keep the type flexible.
- Use a **Factory** to pick the concrete subclass.

This combination gives you reuse from Template Method and openness from Strategy.

## Pitfalls
- Mark the template method `final` to prevent subclasses from accidentally overriding the skeleton.
- Be careful with `applyQualityDelta` for Backstage Passes — the "drop to zero" behaviour does *not* fit the standard clamp formula; override the method rather than hacking the delta.
- `sellIn` is decremented *before* `computeQualityDelta` is called — make sure your delta logic accounts for the already-decremented value.
