/*
 */

package com.googlecode.objectify.test;

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.impl.Path;
import com.googlecode.objectify.impl.translate.EmbeddedClassTranslator;
import com.googlecode.objectify.impl.translate.CreateContext;
import com.googlecode.objectify.impl.translate.EntityClassTranslator;
import com.googlecode.objectify.impl.translate.SaveContext;
import com.googlecode.objectify.test.EvilMemcacheBugTests.SimpleEntity;
import com.googlecode.objectify.test.util.TestBase;
import org.testng.annotations.Test;

import static com.googlecode.objectify.test.util.TestObjectifyService.fact;
import static com.googlecode.objectify.test.util.TestObjectifyService.ofy;

/**
 * Tests of the translators.
 *
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class TranslationTests extends TestBase
{
	@com.googlecode.objectify.annotation.Entity
	public static class SimpleEntityPOJO {
		public @Id Long id;
		public String foo;
	}

	/**
	 */
	@Test
	public void simplePojoEntityTranslates() throws Exception {
		CreateContext createCtx = new CreateContext(fact());
		EntityClassTranslator<SimpleEntityPOJO> translator = new EntityClassTranslator<>(SimpleEntity.class, createCtx);

		SimpleEntityPOJO pojo = new SimpleEntityPOJO();
		pojo.id = 123L;
		pojo.foo = "bar";

		SaveContext saveCtx = new SaveContext(ofy());
		Entity ent = (Entity)translator.save(pojo, false, saveCtx, Path.root());

		assert ent.getKey().getKind().equals(SimpleEntityPOJO.class.getSimpleName());
		assert ent.getKey().getId() == pojo.id;
		assert ent.getProperties().size() == 1;
		assert ent.getProperty("foo").equals("bar");
	}

	public static class Thing {
		public String foo;
	}

	/**
	 */
	@Test
	public void simplePOJOTranslates() throws Exception {
		Path thingPath = Path.root().extend("somewhere");

		CreateContext createCtx = new CreateContext(fact());
		EmbeddedClassTranslator<Thing> translator = new EmbeddedClassTranslator<>(Thing.class, createCtx, thingPath);

		Thing thing = new Thing();
		thing.foo = "bar";

		SaveContext saveCtx = new SaveContext(ofy());
		EmbeddedEntity ent = (EmbeddedEntity)translator.save(thing, false, saveCtx, thingPath);

//		assert ent.getKey().getKind().equals(SimpleEntityPOJO.class.getSimpleName());
//		assert ent.getKey().getId() == pojo.id;
		assert ent.getProperties().size() == 1;
		assert ent.getProperty("foo").equals("bar");
	}
}