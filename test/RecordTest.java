import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RecordTest {
    Map<String, Tuple> data = new HashMap<>();
    Tuple one = new Tuple(1);
    Tuple two = new Tuple(2);
    Tuple three = new Tuple(3);
    {
        data.put("One", one);
        data.put("Two", two);
        data.put("Three", three);
    }

    Record record = new Record(data);

    @Test
    void recordIsAbleToReturnMeOnlyValuesFromColumnsWhichINeed(){
        Record anotherRecord = new Record(record, Arrays.asList("One", "Three"));
        assertEquals(2, anotherRecord.getValues().size());
        assertEquals( one, anotherRecord.getValues().get("One"));
        assertEquals( three, anotherRecord.getValues().get("Three"));
        assertNull(anotherRecord.getValues().get("Two"));
    }

    @Test
    void meetConditionsReturnTrueIfItDoesAndFalseIfNot(){
        Record record = new Record(data);
        List<Condition> conditions = new ArrayList<>();
        Condition condition = new Condition("One", new Tuple(1), null, "=");
        conditions.add(condition);
        assertTrue(record.meetConditions(conditions));

        conditions.clear();
        Condition otherCondition = new Condition("Two", new Tuple(2), "AND", "=");
        conditions.add(otherCondition);
        conditions.add(condition);
        assertTrue(record.meetConditions(conditions));

        conditions.clear();
        Condition falseCondition = new Condition("Three", new Tuple(4), "AND", "=");
        conditions.add(falseCondition);
        conditions.add(otherCondition);
        conditions.add(condition);
        assertFalse(record.meetConditions(conditions));

    }

}