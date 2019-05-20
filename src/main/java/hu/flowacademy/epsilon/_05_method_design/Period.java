package hu.flowacademy.epsilon._05_method_design;

import java.util.*;

/**
 * This class shows how can you design methods for input validation and how you
 * must use defensive copying to ensure you don't accidentally expose your object's
 * state if its state consists of mutable objects.
 * NOTE: This is a didactic example. In modern code, you should not use java.util.Date
 * for any purpose; it's an old API riddled with problems. Classes in the
 * {@code java.time} package are the preferred way to manage time-based values.
 * Specifically, our main problem here with Date is that its instances are mutable. This
 * means that accepting Date objects into our objects' state and handing out Date objects
 * from our objects' state are both dangerous.
 */
public class Period {
    // This is a comparator that compares periods using their start values.
    // Note this is possible because Date objects themselves implement Comparable.
    public static final Comparator<Period> START_COMPARATOR =
            Comparator.comparing(Period::getStart);

    // This is a comparator that compares periods using their start values and
    // if they're equal, then by their end values. Notice we use
    // Comparator.thenComparing to create a composite comparator based on two
    // values.
    public static final Comparator<Period> START_END_COMPARATOR =
            Comparator.comparing(Period::getStart).thenComparing(Period::getEnd);

    // This is a comparator that compares periods based on their duration. Note we
    // had to use Comparator.compareLong instead of Comparator.compare here as the
    // duration is a Long.
    public static final Comparator<Period> DURATION_COMPARATOR =
        Comparator.comparingLong(Period::durationMillis);

    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        // 1. Defend against nulls. There is rarely a valid reason to accept null
        //    values into your object's state.
        // 2. Defensively copy every input value. Note we aren't using Date.clone
        //    to create copies, as we can't be sure somebody isn't passing us a
        //    malicious subclass that will change its values after cloning. Therefore
        //    we create new Date objects using the constructor. If you are accepting
        //    arrays, you must copy them too, but for them it's safe (and fastest) to
        //    clone them.
        this.start = new Date(Objects.requireNonNull(start, "start").getTime());
        this.end = new Date(Objects.requireNonNull(end, "end").getTime());

        // Now we validate the parameters. We enforce that end can't be earlier than
        // start. Note that we validated the parameters *after* we copied them. This too
        // is defensive: we can be sure that our copies won't change after they passed
        // the validity checks.
        if (this.start.getTime() > this.end.getTime()) {
            throw new IllegalArgumentException("start > end");
        }
        // Other typical Java built-in parameter validation exceptions you can throw in
        // appropriate circumstances:
        // - NullPointerException (preferrably using above demonstrated requireNonNull).
        // - IndexOutOfBoundsException
        // - NoSuchElementException
        // - IllegalStateException (not in constructors. Recommended if the object's state
        //   prevents it from correctly performing an operation.)
        // - UnsupportedOperationException (not in constructors; typically used when an
        //   object only partially implements an interface. E.g. read-only lists throw this
        //   from all methods that would modify it.)
    }

    public Date getStart() {
        // Defensively copy before returning.
        return new Date(start.getTime());
    }

    public Date getEnd() {
        // Defensively copy before returning.
        return new Date(end.getTime());
    }

    public long durationMillis() {
        return end.getTime() - start.getTime();
    }

    @Override
    public String toString() {
        // NOTE: if we allowed null for start and end, we'd have to use String.valueOf(start)
        // etc. instead.
        return "Period(" + start.toString() + "-" + end.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Period) {
            Period other = (Period) obj;
            // NOTE: if we allowed null for start and end, we'd have to use
            // Objects.equals(start, other.start) etc. Since we reject nulls
            // in the constructor, we can confidently just call equals on them
            // as we know we can't get NullPointerException.
            return start.equals(other.start) && end.equals(other.end);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // NOTE: if we allowed null for start and end, we'd have to use
        // Objects.hash(start, end) here. Also note that Objects.hash is
        // a VARIABLE-ARITY method, meaning it can accept any number of
        // arguments; every invocation is then constructing an array to hold
        // these arguments. Use variable-arity methods with this in mind.
        return start.hashCode() + 31 * end.hashCode();
    }
}
