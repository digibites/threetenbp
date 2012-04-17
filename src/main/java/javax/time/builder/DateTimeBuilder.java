/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.builder;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.LocalTime;

/**
 * Builder that can combine date and time fields into date and time objects.
 * <p>
 * This class is mutable and not thread-safe.
 * It should only be used from a single thread.
 */
public final class DateTimeBuilder implements CalendricalObject {

    /**
     * The map of other fields.
     */
    private Map<DateTimeField, Long> otherFields;
    /**
     * The map of date fields.
     */
    private final EnumMap<LocalDateField, Long> dateFields = new EnumMap<LocalDateField, Long>(LocalDateField.class);
    /**
     * The map of time fields.
     */
    private final EnumMap<LocalTimeField, Long> timeFields = new EnumMap<LocalTimeField, Long>(LocalTimeField.class);
    /**
     * The map of calendrical objects by type.
     */
    private final Map<Class<?>, Object> objects = new HashMap<Class<?>, Object>();

    /**
     * Creates an empty instance of the builder.
     */
    public DateTimeBuilder() {
    }

    //-----------------------------------------------------------------------
    public boolean containsFieldValue(DateTimeField field) {
        DateTimes.checkNotNull(field, "Field cannot be null");
        return otherFields.containsKey(field);
    }

    public long getFieldValue(DateTimeField field) {
        DateTimes.checkNotNull(field, "Field cannot be null");
        Long value = otherFields.get(field);
        if (value == null) {
            throw new CalendricalException("Field not found: " + field);
        }
        return value;
    }

    public DateTimeBuilder addFieldValue(DateTimeField field, long value) {
        DateTimes.checkNotNull(field, "Field cannot be null");
        Long old;
        if (field instanceof LocalDateField) {
            old = dateFields.put((LocalDateField) field, value);
        } else if (field instanceof LocalTimeField) {
            old = timeFields.put((LocalTimeField) field, value);
        } else {
            if (otherFields == null) {
                otherFields = new LinkedHashMap<DateTimeField, Long>();
            }
            old = otherFields.put(field, value);
        }
        if (old != null && old.longValue() != value) {
            throw new CalendricalException("Conflict found: " + field + " " + old + " vs " + value);
        }
        return this;
    }

    public long removeFieldValue(DateTimeField field) {
        DateTimes.checkNotNull(field, "Field cannot be null");
        Long value = otherFields.remove(field);
        if (value == null) {
            throw new CalendricalException("Field not found: " + field);
        }
        return value;
    }

//    public long build() {
//        return 0;
//    }
//
//    public LocalDate buildLocalDate() {
//        if (hasAllFields(YEAR, MONTH_OF_YEAR, DAY_OF_MONTH)) {
//            return LocalDate.of(getInt(YEAR), getInt(MONTH_OF_YEAR), getInt(DAY_OF_MONTH));
//        } else if (hasAllFields(EPOCH_DAY)) {
//            return LocalDate.ofEpochDay(fields.get(EPOCH_DAY));
//        } else if (hasAllFields(YEAR, DAY_OF_YEAR)) {
//            return LocalDate.ofYearDay(getInt(YEAR), getInt(DAY_OF_YEAR));
//        }
//        throw new CalendricalException("Unable to build Date due to missing fields"); // TODO
//    }
//    
//    public ChronoDate<?> buildChronoDate(Chrono chrono) {
//        if (hasAllFields(ChronoDateField.PROLEPTIC_YEAR, ChronoDateField.MONTH_OF_YEAR, ChronoDateField.DAY_OF_MONTH)) {
//            return chrono.date(getInt(ChronoDateField.PROLEPTIC_YEAR), getInt(ChronoDateField.MONTH_OF_YEAR), getInt(ChronoDateField.DAY_OF_MONTH));
//        } else if (hasAllFields(ChronoDateField.ERA, ChronoDateField.YEAR_OF_ERA, ChronoDateField.MONTH_OF_YEAR, ChronoDateField.DAY_OF_MONTH)) {
//            // TODO: fix the Era situation
//            return chrono.date(null, getInt(ChronoDateField.YEAR_OF_ERA), getInt(ChronoDateField.MONTH_OF_YEAR), getInt(ChronoDateField.DAY_OF_MONTH));
//        } else {
//            return chrono.date(buildLocalDate());
//        }
//    }
//
//    public LocalTime buildLocalTime() {
//        boolean normalFields = hasAllFields(HOUR_OF_DAY, MINUTE_OF_HOUR);
//        boolean uptoSecond = normalFields && fields.containsKey(SECOND_OF_MINUTE);
//        boolean hasNano = fields.containsKey(NANO_OF_SECOND);
//        if (uptoSecond && hasNano) {
//            return LocalTime.of(getInt(HOUR_OF_DAY), getInt(MINUTE_OF_HOUR), getInt(SECOND_OF_MINUTE), getInt(NANO_OF_SECOND));
//        } else if (uptoSecond) {
//            return LocalTime.of(getInt(HOUR_OF_DAY), getInt(MINUTE_OF_HOUR), getInt(SECOND_OF_MINUTE));
//        } else if (normalFields) {
//            return LocalTime.of(getInt(HOUR_OF_DAY), getInt(MINUTE_OF_HOUR));
//        } else if (fields.containsKey(NANO_OF_DAY)) {
//            return LocalTime.ofNanoOfDay(fields.get(NANO_OF_DAY));
//        }
//        throw new CalendricalException("Unable to build Time due to missing fields"); // TODO
//    }
//    
//    public LocalDateTime buildLocalDateTime() {
//        return LocalDateTime.of(buildLocalDate(), buildLocalTime());
//    }
//    
//    int getInt(DateTimeField field) {
//        return DateTimes.safeToInt(fields.get(field));
//    }
//
//    boolean hasAllFields(DateTimeField ... fields) {
//        for (DateTimeField field : fields) {
//            if (!this.fields.containsKey(field)) {
//                return false;
//            }
//        }
//        return true;
//    }

    public long[] getValues(DateTimeField... fields) {
        long[] values = new long[fields.length];
        for (DateTimeField field : fields) {
            if (this.otherFields.containsKey(field) == false) {
                return null;
            }
        }
        return values;
    }

    public DateTimeBuilder resolve() {
        // handle unusual fields
        if (otherFields != null) {
            for (Entry<DateTimeField, Long> entry : otherFields.entrySet()) {
                entry.getKey().getDateTimeRules().resolve(this, entry.getValue());  // TODO restart
            }
        }
        // handle standard fields
        mergeDate();
        mergeTime();
        return this;
    }

    private void mergeDate() {
        if (dateFields.containsKey(LocalDateField.EPOCH_MONTH)) {
            long em = dateFields.remove(LocalDateField.EPOCH_MONTH);
            addFieldValue(LocalDateField.MONTH_OF_YEAR, (em % 12) + 1);
            addFieldValue(LocalDateField.YEAR, (em / 12) + 1970);
        }
        
        if (dateFields.containsKey(LocalDateField.YEAR)) {
            if (dateFields.containsKey(LocalDateField.DAY_OF_MONTH) && dateFields.containsKey(LocalDateField.MONTH_OF_YEAR)) {
                int dom = DateTimes.safeToInt(dateFields.remove(LocalDateField.DAY_OF_MONTH));
                int moy = DateTimes.safeToInt(dateFields.remove(LocalDateField.MONTH_OF_YEAR));
                int y = DateTimes.safeToInt(dateFields.remove(LocalDateField.YEAR));
                addResolved(LocalDate.of(y, moy, dom));
            }
//            if (dateFields.containsKey(LocalDateField.DAY_OF_YEAR)) {
//                int moy = DateTimes.safeToInt(dateFields.remove(LocalDateField.MONTH_OF_YEAR));
//                int y = DateTimes.safeToInt(dateFields.remove(LocalDateField.YEAR));
//                addResolved(LocalDate.ofYearDay(y, doy));
//            }
        }
//            int dom = DateTimes.safeToInt(dateFields.get(LocalDateField.DAY_OF_MONTH));
//            if (dateFields.containsKey(LocalDateField.MONTH_OF_YEAR) && dateFields.containsKey(LocalDateField.YEAR)) {
//                int moy = DateTimes.safeToInt(dateFields.get(LocalDateField.MONTH_OF_YEAR));
//                int y = DateTimes.safeToInt(dateFields.get(LocalDateField.YEAR));
//                addResolved(LocalDate.of(y, moy, dom));
//            }
//            if (dateFields.containsKey(LocalDateField.EPOCH_MONTH)) {
//                long em = dateFields.get(LocalDateField.EPOCH_MONTH);
//                addResolved(LocalDate.of(DateTimes.safeToInt((em / 12) + 1970), (int) ((em % 12) + 1), dom));
//            }
        
        if (dateFields.containsKey(LocalDateField.EPOCH_DAY)) {
            addResolved(LocalDate.ofEpochDay(dateFields.remove(LocalDateField.EPOCH_DAY)));
        }
    }

    private void mergeTime() {
        if (timeFields.containsKey(LocalTimeField.CLOCK_HOUR_OF_DAY)) {
            long ch = timeFields.remove(LocalTimeField.CLOCK_HOUR_OF_DAY);
            addFieldValue(LocalTimeField.HOUR_OF_DAY, ch == 24 ? 0 : ch);
        }
        if (timeFields.containsKey(LocalTimeField.CLOCK_HOUR_OF_AMPM)) {
            long ch = timeFields.remove(LocalTimeField.CLOCK_HOUR_OF_AMPM);
            addFieldValue(LocalTimeField.HOUR_OF_AMPM, ch == 12 ? 0 : ch);
        }
        if (timeFields.containsKey(LocalTimeField.AMPM_OF_DAY) && timeFields.containsKey(LocalTimeField.HOUR_OF_AMPM)) {
            long ap = timeFields.remove(LocalTimeField.AMPM_OF_DAY);
            long hap = timeFields.remove(LocalTimeField.HOUR_OF_AMPM);
            addFieldValue(LocalTimeField.HOUR_OF_DAY, ap * 2 + hap);
        }
//        if (timeFields.containsKey(LocalTimeField.HOUR_OF_DAY) && timeFields.containsKey(LocalTimeField.MINUTE_OF_HOUR)) {
//            long hod = timeFields.remove(LocalTimeField.HOUR_OF_DAY);
//            long moh = timeFields.remove(LocalTimeField.MINUTE_OF_HOUR);
//            addFieldValue(LocalTimeField.MINUTE_OF_DAY, hod * 60 + moh);
//        }
//        if (timeFields.containsKey(LocalTimeField.MINUTE_OF_DAY) && timeFields.containsKey(LocalTimeField.SECOND_OF_MINUTE)) {
//            long mod = timeFields.remove(LocalTimeField.MINUTE_OF_DAY);
//            long som = timeFields.remove(LocalTimeField.SECOND_OF_MINUTE);
//            addFieldValue(LocalTimeField.SECOND_OF_DAY, mod * 60 + som);
//        }
        if (timeFields.containsKey(LocalTimeField.NANO_OF_DAY)) {
            long nod = timeFields.remove(LocalTimeField.NANO_OF_DAY);
            addFieldValue(LocalTimeField.SECOND_OF_DAY, nod / 1000000000L);
            addFieldValue(LocalTimeField.NANO_OF_SECOND, nod % 1000000000L);
        }
        if (timeFields.containsKey(LocalTimeField.MICRO_OF_DAY)) {
            long cod = timeFields.remove(LocalTimeField.MICRO_OF_DAY);
            addFieldValue(LocalTimeField.SECOND_OF_DAY, cod / 1000000);
            addFieldValue(LocalTimeField.MICRO_OF_SECOND, cod % 1000000);
        }
        if (timeFields.containsKey(LocalTimeField.MILLI_OF_DAY)) {
            long lod = timeFields.remove(LocalTimeField.MILLI_OF_DAY);
            addFieldValue(LocalTimeField.SECOND_OF_DAY, lod / 1000);
            addFieldValue(LocalTimeField.MILLI_OF_SECOND, lod % 1000);
        }
        if (timeFields.containsKey(LocalTimeField.SECOND_OF_DAY)) {
            long sod = timeFields.remove(LocalTimeField.SECOND_OF_DAY);
            addFieldValue(LocalTimeField.HOUR_OF_DAY, sod / 3600);
            addFieldValue(LocalTimeField.MINUTE_OF_HOUR, (sod / 60) % 60);
            addFieldValue(LocalTimeField.SECOND_OF_MINUTE, sod % 60);
        }
        if (timeFields.containsKey(LocalTimeField.MINUTE_OF_DAY)) {
            long mod = timeFields.remove(LocalTimeField.MINUTE_OF_DAY);
            addFieldValue(LocalTimeField.HOUR_OF_DAY, mod / 60);
            addFieldValue(LocalTimeField.MINUTE_OF_HOUR, mod % 60);
        }
        
//            long sod = nod / 1000000000L;
//            addFieldValue(LocalTimeField.HOUR_OF_DAY, sod / 3600);
//            addFieldValue(LocalTimeField.MINUTE_OF_HOUR, (sod / 60) % 60);
//            addFieldValue(LocalTimeField.SECOND_OF_MINUTE, sod % 60);
//            addFieldValue(LocalTimeField.NANO_OF_SECOND, nod % 1000000000L);
        if (timeFields.containsKey(LocalTimeField.MILLI_OF_SECOND) && timeFields.containsKey(LocalTimeField.MICRO_OF_SECOND)) {
            long los = timeFields.remove(LocalTimeField.MILLI_OF_SECOND);
            long cos = timeFields.get(LocalTimeField.MICRO_OF_SECOND);
            addFieldValue(LocalTimeField.MICRO_OF_SECOND, los * 1000 + (cos % 1000));
        }
        
        Long hod = timeFields.get(LocalTimeField.HOUR_OF_DAY);
        Long moh = timeFields.get(LocalTimeField.MINUTE_OF_HOUR);
        Long som = timeFields.get(LocalTimeField.SECOND_OF_MINUTE);
        if (hod != null) {
            int hodVal = DateTimes.safeToInt(hod);
            if (moh != null) {
                int mohVal = DateTimes.safeToInt(hod);
                if (som != null) {
                    int somVal = DateTimes.safeToInt(hod);
                    addResolved(LocalTime.of(hodVal, mohVal, somVal));
                } else {
                    addResolved(LocalTime.of(hodVal, mohVal));
                }
            } else {
                addResolved(LocalTime.of(hodVal, 0));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T extract(Class<T> type) {
        Object obj = objects.get(type);
        if (obj != null) {
            return (T) obj;
        }
        return null;
    }

    public void addResolved(CalendricalObject calendrical) {
        Class<?> cls = calendrical.getClass();
        Object obj = objects.get(cls);
        if (obj != null && obj.equals(calendrical) == false) {
            throw new CalendricalException("DateTime resolve found a conflict: " + obj + " vs " + calendrical);
        }
    }

}
