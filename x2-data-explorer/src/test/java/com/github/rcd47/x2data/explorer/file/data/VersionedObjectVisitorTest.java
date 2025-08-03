package com.github.rcd47.x2data.explorer.file.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.rcd47.x2data.lib.unreal.mappings.UnrealName;

import javafx.scene.control.TreeItem;

public class VersionedObjectVisitorTest {
	
	@Test
	public void testStaticArrayWithFirstIndexZero() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitIntValue(11);
		visitor.visitProperty(new UnrealName("a"), 2);
		visitor.visitIntValue(22);
		visitor.visitStructEnd();
		
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitIntValue(33);
		visitor.visitProperty(new UnrealName("a"), 1);
		visitor.visitIntValue(44);
		visitor.visitProperty(new UnrealName("a"), 4);
		visitor.visitIntValue(55);
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(11, null, 22)));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(33, 44, 22, null, 55)));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 11, FieldChangeType.ADDED, null, 33, Integer.MIN_VALUE, 2));
		var treeA2 = findTreeItem(treeA, 0, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), 22, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		// tree node at frame 2 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 4, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 33, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
		var treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), 44, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeA2 = findTreeItem(treeA, 0, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), 22, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		var treeA4 = findTreeItem(treeA, 0, "4");
		assertThat(treeA4.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("4"), 55, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		// tree node at frame 2 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 3, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 33, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
		treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), 44, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeA4 = findTreeItem(treeA, 0, "4");
		assertThat(treeA4.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("4"), 55, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
	}
	
	@Test
	public void testStaticArrayWithFirstIndexNonZero() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 2);
		visitor.visitIntValue(11);
		visitor.visitStructEnd();
		
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitIntValue(22);
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(null, null, 11)));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(22, null, 11)));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA2 = findTreeItem(treeA, 0, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), 11, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		// tree node at frame 2 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		var treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 22, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeA2 = findTreeItem(treeA, 0, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), 11, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		// tree node at frame 2 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 22, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
	}
	
	@Test
	public void testStaticArrayIsNotChangedButOtherStructMembersAreChanged() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 2);
		visitor.visitIntValue(11);
		visitor.visitProperty(new UnrealName("b"), 0);
		visitor.visitIntValue(22);
		visitor.visitStructEnd();
		
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 2);
		visitor.visitIntValue(11);
		visitor.visitProperty(new UnrealName("b"), 0);
		visitor.visitIntValue(33);
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(null, null, 11), "b", 22));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(null, null, 11), "b", 33));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(2);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		var treeA2 = findTreeItem(treeA, 0, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), 11, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		var treeB = findTreeItem(treeRoot, 0, "b");
		assertThat(treeB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("b"), 22, FieldChangeType.ADDED, null, 33, Integer.MIN_VALUE, 2));
		
		// tree node at frame 2 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getChildren()).hasSize(2);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeA2 = findTreeItem(treeA, 0, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), 11, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeB = findTreeItem(treeRoot, 0, "b");
		assertThat(treeB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("b"), 33, FieldChangeType.CHANGED, 22, null, 1, Integer.MAX_VALUE));
		
		// tree node at frame 2 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeB = findTreeItem(treeRoot, 0, "b");
		assertThat(treeB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("b"), 33, FieldChangeType.CHANGED, 22, null, 1, Integer.MAX_VALUE));
	}
	
	@Test
	public void testStaticArrayWithNestedStruct() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 2);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(11);
		visitor.visitStructEnd();
		visitor.visitStructEnd();
		
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 1);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(33);
		visitor.visitStructEnd();
		visitor.visitStructEnd();
		
		visitor.setRootObject(3, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 2);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(55);
		visitor.visitStructEnd();
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(null, null, Map.of("aa", 11))));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(null, Map.of("aa", 33), Map.of("aa", 11))));
		assertThat(vmap.getValueAt(3)).usingRecursiveComparison().isEqualTo(Map.of("a", Arrays.asList(null, Map.of("aa", 33), Map.of("aa", 55))));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA2 = findTreeItem(treeA, 1, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 3));
		var treeA2AA = findTreeItem(treeA2, 0, "aa");
		assertThat(treeA2AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 11, FieldChangeType.ADDED, null, 55, Integer.MIN_VALUE, 3));
		
		// tree node at frame 2 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		var treeA1 = findTreeItem(treeA, 1, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		var treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 33, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeA2 = findTreeItem(treeA, 1, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), null, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, 3));
		treeA2AA = findTreeItem(treeA2, 0, "aa");
		assertThat(treeA2AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 11, FieldChangeType.NONE, null, 55, Integer.MIN_VALUE, 3));
		
		// tree node at frame 2 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA1 = findTreeItem(treeA, 1, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 33, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		// tree node at frame 3 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 3, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA1 = findTreeItem(treeA, 1, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 33, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeA2 = findTreeItem(treeA, 1, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA2AA = findTreeItem(treeA2, 0, "aa");
		assertThat(treeA2AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 55, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
		
		// tree node at frame 3 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 3, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA2 = findTreeItem(treeA, 1, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA2AA = findTreeItem(treeA2, 0, "aa");
		assertThat(treeA2AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 55, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
	}
	
	@Test
	public void testNestedStructs() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aaa"), 0);
		visitor.visitStringValue("mmm");
		visitor.visitStructEnd();
		visitor.visitProperty(new UnrealName("ab"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aba"), 0);
		visitor.visitStringValue("ppp");
		visitor.visitStructEnd();
		visitor.visitStructEnd();
		visitor.visitProperty(new UnrealName("b"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("ba"), 0);
		visitor.visitStringValue("zzz");
		visitor.visitProperty(new UnrealName("bb"), 0);
		visitor.visitStringValue("vvv");
		visitor.visitStructEnd();
		visitor.visitStructEnd();
		
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aaa"), 0);
		visitor.visitStringValue("xxx");
		visitor.visitStructEnd();
		visitor.visitStructEnd();
		visitor.visitProperty(new UnrealName("b"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("bb"), 0);
		visitor.visitStringValue("uuu");
		visitor.visitStructEnd();
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(
				Map.of("a", Map.of("aa", Map.of("aaa", "mmm"), "ab", Map.of("aba", "ppp")), "b", Map.of("ba", "zzz", "bb", "vvv")));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(
				Map.of("a", Map.of("aa", Map.of("aaa", "xxx"), "ab", Map.of("aba", "ppp")), "b", Map.of("ba", "zzz", "bb", "uuu")));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(2);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeAA = findTreeItem(treeA, 1, "aa");
		assertThat(treeAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeAAA = findTreeItem(treeAA, 0, "aaa");
		assertThat(treeAAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aaa"), "mmm", FieldChangeType.ADDED, null, "xxx", Integer.MIN_VALUE, 2));
		var treeAB = findTreeItem(treeA, 1, "ab");
		assertThat(treeAB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		var treeABA = findTreeItem(treeAB, 0, "aba");
		assertThat(treeABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), "ppp", FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		var treeB = findTreeItem(treeRoot, 2, "b");
		assertThat(treeB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("b"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeBA = findTreeItem(treeB, 0, "ba");
		assertThat(treeBA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ba"), "zzz", FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		var treeBB = findTreeItem(treeB, 0, "bb");
		assertThat(treeBB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("bb"), "vvv", FieldChangeType.ADDED, null, "uuu", Integer.MIN_VALUE, 2));
		
		// tree node at frame 2 when onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeAA = findTreeItem(treeA, 1, "aa");
		assertThat(treeAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeAAA = findTreeItem(treeAA, 0, "aaa");
		assertThat(treeAAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aaa"), "xxx", FieldChangeType.CHANGED, "mmm", null, 1, Integer.MAX_VALUE));
		treeAB = findTreeItem(treeA, 1, "ab");
		assertThat(treeAB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeABA = findTreeItem(treeAB, 0, "aba");
		assertThat(treeABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), "ppp", FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeB = findTreeItem(treeRoot, 2, "b");
		assertThat(treeB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("b"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeBA = findTreeItem(treeB, 0, "ba");
		assertThat(treeBA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ba"), "zzz", FieldChangeType.NONE, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		treeBB = findTreeItem(treeB, 0, "bb");
		assertThat(treeBB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("bb"), "uuu", FieldChangeType.CHANGED, "vvv", null, 1, Integer.MAX_VALUE));
		
		// tree node at frame 2 when onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, true);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeAA = findTreeItem(treeA, 1, "aa");
		assertThat(treeAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeAAA = findTreeItem(treeAA, 0, "aaa");
		assertThat(treeAAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aaa"), "xxx", FieldChangeType.CHANGED, "mmm", null, 1, Integer.MAX_VALUE));
		treeB = findTreeItem(treeRoot, 1, "b");
		assertThat(treeB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("b"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeBB = findTreeItem(treeB, 0, "bb");
		assertThat(treeBB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("bb"), "uuu", FieldChangeType.CHANGED, "vvv", null, 1, Integer.MAX_VALUE));
	}
	
	@Test
	public void testStructsInsideMap() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitMapStart(1);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aaa"), 0);
		visitor.visitStringValue("mmm");
		visitor.visitStructEnd();
		visitor.visitProperty(new UnrealName("ab"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aba"), 0);
		visitor.visitStringValue("ppp");
		visitor.visitStructEnd();
		visitor.visitMapEnd();
		visitor.visitProperty(new UnrealName("b"), 0);
		visitor.visitMapStart(1);
		visitor.visitProperty(new UnrealName("ba"), 0);
		visitor.visitStringValue("zzz");
		visitor.visitProperty(new UnrealName("bb"), 0);
		visitor.visitStringValue("vvv");
		visitor.visitMapEnd();
		visitor.visitStructEnd();
		
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitMapStart(1);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aaa"), 0);
		visitor.visitStringValue("xxx");
		visitor.visitStructEnd();
		visitor.visitMapEnd();
		visitor.visitProperty(new UnrealName("b"), 0);
		visitor.visitMapStart(1);
		visitor.visitProperty(new UnrealName("bb"), 0);
		visitor.visitStringValue("uuu");
		visitor.visitMapEnd();
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(
				Map.of("a", Map.of("aa", Map.of("aaa", "mmm"), "ab", Map.of("aba", "ppp")), "b", Map.of("ba", "zzz", "bb", "vvv")));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(
				Map.of("a", Map.of("aa", Map.of("aaa", "xxx")), "b", Map.of("bb", "uuu")));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(2);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeAA = findTreeItem(treeA, 1, "aa");
		assertThat(treeAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeAAA = findTreeItem(treeAA, 0, "aaa");
		assertThat(treeAAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aaa"), "mmm", FieldChangeType.ADDED, null, "xxx", Integer.MIN_VALUE, 2));
		var treeAB = findTreeItem(treeA, 1, "ab");
		assertThat(treeAB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeABA = findTreeItem(treeAB, 0, "aba");
		assertThat(treeABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), "ppp", FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeB = findTreeItem(treeRoot, 2, "b");
		assertThat(treeB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("b"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeBA = findTreeItem(treeB, 0, "ba");
		assertThat(treeBA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ba"), "zzz", FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeBB = findTreeItem(treeB, 0, "bb");
		assertThat(treeBB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("bb"), "vvv", FieldChangeType.ADDED, null, "uuu", Integer.MIN_VALUE, 2));
		
		// tree node at frame 2
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeAA = findTreeItem(treeA, 1, "aa");
		assertThat(treeAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeAAA = findTreeItem(treeAA, 0, "aaa");
		assertThat(treeAAA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aaa"), "xxx", FieldChangeType.CHANGED, "mmm", null, 1, Integer.MAX_VALUE));
		treeAB = findTreeItem(treeA, 1, "ab");
		assertThat(treeAB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.REMOVED, null, null, 1, Integer.MAX_VALUE));
		treeABA = findTreeItem(treeAB, 0, "aba");
		assertThat(treeABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), null, FieldChangeType.REMOVED, "ppp", null, 1, Integer.MAX_VALUE));
		treeB = findTreeItem(treeRoot, 2, "b");
		assertThat(treeB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("b"), null, FieldChangeType.CHANGED, null, null, 1, Integer.MAX_VALUE));
		treeBA = findTreeItem(treeB, 0, "ba");
		assertThat(treeBA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ba"), null, FieldChangeType.REMOVED, "zzz", null, 1, Integer.MAX_VALUE));
		treeBB = findTreeItem(treeB, 0, "bb");
		assertThat(treeBB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("bb"), "uuu", FieldChangeType.CHANGED, "vvv", null, 1, Integer.MAX_VALUE));
	}
	
	@Test
	public void testDynamicArrayWithPrimitives() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(2);
		visitor.visitIntValue(11);
		visitor.visitIntValue(22);
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitIntValue(33);
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		visitor.setRootObject(3, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitIntValue(33);
		visitor.visitIntValue(44);
		visitor.visitIntValue(55);
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(11, 22)));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(33)));
		assertThat(vmap.getValueAt(3)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(33, 44, 55)));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 11, FieldChangeType.ADDED, null, 33, Integer.MIN_VALUE, 2));
		var treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), 22, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		
		// tree node at frame 2
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 33, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
		treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.REMOVED, 22, 44, 1, 3));
		
		// tree node at frame 3 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 3, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 3, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 33, FieldChangeType.NONE, 11, null, 1, Integer.MAX_VALUE));
		treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), 44, FieldChangeType.ADDED, null, null, 2, Integer.MAX_VALUE));
		var treeA2 = findTreeItem(treeA, 0, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), 55, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
		
		// tree node at frame 3 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 3, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), 44, FieldChangeType.ADDED, null, null, 2, Integer.MAX_VALUE));
		treeA2 = findTreeItem(treeA, 0, "2");
		assertThat(treeA2.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("2"), 55, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE));
	}
	
	@Test
	public void testDynamicArrayWithStructs() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		// frame 1: two elements created
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(2);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(11);
		visitor.visitProperty(new UnrealName("ab"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aba"), 0);
		visitor.visitIntValue(22);
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(33);
		visitor.visitProperty(new UnrealName("ab"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aba"), 0);
		visitor.visitIntValue(44);
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		// frame 2: both elements changed. first element has a property different, second element has a nested array different.
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(2);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(55);
		visitor.visitProperty(new UnrealName("ab"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aba"), 0);
		visitor.visitIntValue(22);
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(33);
		visitor.visitProperty(new UnrealName("ab"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aba"), 0);
		visitor.visitIntValue(66);
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		// frame 3: first element is unchanged, second element is removed
		visitor.setRootObject(3, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(55);
		visitor.visitProperty(new UnrealName("ab"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aba"), 0);
		visitor.visitIntValue(22);
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		// frame 4: first element has struct array removed, second element is re-added
		visitor.setRootObject(4, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(2);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(55);
		visitor.visitStructEnd();
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aa"), 0);
		visitor.visitIntValue(33);
		visitor.visitProperty(new UnrealName("ab"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("aba"), 0);
		visitor.visitIntValue(66);
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(
				Map.of("aa", 11, "ab", List.of(Map.of("aba", 22))),
				Map.of("aa", 33, "ab", List.of(Map.of("aba", 44))))));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(
				Map.of("aa", 55, "ab", List.of(Map.of("aba", 22))),
				Map.of("aa", 33, "ab", List.of(Map.of("aba", 66))))));
		assertThat(vmap.getValueAt(3)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(
				Map.of("aa", 55, "ab", List.of(Map.of("aba", 22))))));
		assertThat(vmap.getValueAt(4)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(
				Map.of("aa", 55),
				Map.of("aa", 33, "ab", List.of(Map.of("aba", 66))))));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA0 = findTreeItem(treeA, 2, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA0AA = findTreeItem(treeA0, 0, "aa");
		assertThat(treeA0AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 11, FieldChangeType.ADDED, null, 55, Integer.MIN_VALUE, 2));
		var treeA0AB = findTreeItem(treeA0, 1, "ab");
		assertThat(treeA0AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 4));
		var treeA0AB0 = findTreeItem(treeA0AB, 1, "0");
		assertThat(treeA0AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 4));
		var treeA0AB0ABA = findTreeItem(treeA0AB0, 0, "aba");
		assertThat(treeA0AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), 22, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 4));
		var treeA1 = findTreeItem(treeA, 2, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 33, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 3));
		var treeA1AB = findTreeItem(treeA1, 1, "ab");
		assertThat(treeA1AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA1AB0 = findTreeItem(treeA1AB, 1, "0");
		assertThat(treeA1AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA1AB0ABA = findTreeItem(treeA1AB0, 0, "aba");
		assertThat(treeA1AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), 44, FieldChangeType.ADDED, null, 66, Integer.MIN_VALUE, 2));
		
		// tree node at frame 2 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA0 = findTreeItem(treeA, 2, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.CHANGED, null, null, 1, 4));
		treeA0AA = findTreeItem(treeA0, 0, "aa");
		assertThat(treeA0AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 55, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
		treeA0AB = findTreeItem(treeA0, 1, "ab");
		assertThat(treeA0AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, 4));
		treeA0AB0 = findTreeItem(treeA0AB, 1, "0");
		assertThat(treeA0AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, 4));
		treeA0AB0ABA = findTreeItem(treeA0AB0, 0, "aba");
		assertThat(treeA0AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), 22, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, 4));
		treeA1 = findTreeItem(treeA, 2, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 33, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, 3));
		treeA1AB = findTreeItem(treeA1, 1, "ab");
		assertThat(treeA1AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA1AB0 = findTreeItem(treeA1AB, 1, "0");
		assertThat(treeA1AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA1AB0ABA = findTreeItem(treeA1AB0, 0, "aba");
		assertThat(treeA1AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), 66, FieldChangeType.CHANGED, 44, null, 1, 3));
		
		// tree node at frame 2 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA0 = findTreeItem(treeA, 1, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.CHANGED, null, null, 1, 4));
		treeA0AA = findTreeItem(treeA0, 0, "aa");
		assertThat(treeA0AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 55, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
		treeA1 = findTreeItem(treeA, 1, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA1AB = findTreeItem(treeA1, 1, "ab");
		assertThat(treeA1AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA1AB0 = findTreeItem(treeA1AB, 1, "0");
		assertThat(treeA1AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA1AB0ABA = findTreeItem(treeA1AB0, 0, "aba");
		assertThat(treeA1AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), 66, FieldChangeType.CHANGED, 44, null, 1, 3));
		
		// tree node at frame 3 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 3, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 2, 4));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 2, 4));
		treeA0 = findTreeItem(treeA, 2, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.NONE, null, null, 1, 4));
		treeA0AA = findTreeItem(treeA0, 0, "aa");
		assertThat(treeA0AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 55, FieldChangeType.NONE, 11, null, 1, Integer.MAX_VALUE));
		treeA0AB = findTreeItem(treeA0, 1, "ab");
		assertThat(treeA0AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, 4));
		treeA0AB0 = findTreeItem(treeA0AB, 1, "0");
		assertThat(treeA0AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, 4));
		treeA0AB0ABA = findTreeItem(treeA0AB0, 0, "aba");
		assertThat(treeA0AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), 22, FieldChangeType.NONE, null, null, Integer.MIN_VALUE, 4));
		treeA1 = findTreeItem(treeA, 2, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.REMOVED, null, null, 2, 4));
		treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), null, FieldChangeType.REMOVED, 33, 33, 1, 4));
		treeA1AB = findTreeItem(treeA1, 1, "ab");
		assertThat(treeA1AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.REMOVED, null, null, 2, 4));
		treeA1AB0 = findTreeItem(treeA1AB, 1, "0");
		assertThat(treeA1AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.REMOVED, null, null, 2, 4));
		treeA1AB0ABA = findTreeItem(treeA1AB0, 0, "aba");
		assertThat(treeA1AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), null, FieldChangeType.REMOVED, 66, 66, 2, 4));
		
		// tree node at frame 3 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 3, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 2, 4));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 2, 4));
		treeA1 = findTreeItem(treeA, 2, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.REMOVED, null, null, 2, 4));
		treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), null, FieldChangeType.REMOVED, 33, 33, 1, 4));
		treeA1AB = findTreeItem(treeA1, 1, "ab");
		assertThat(treeA1AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.REMOVED, null, null, 2, 4));
		treeA1AB0 = findTreeItem(treeA1AB, 1, "0");
		assertThat(treeA1AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.REMOVED, null, null, 2, 4));
		treeA1AB0ABA = findTreeItem(treeA1AB0, 0, "aba");
		assertThat(treeA1AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), null, FieldChangeType.REMOVED, 66, 66, 2, 4));
		
		// tree node at frame 4 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 4, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 3, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 3, Integer.MAX_VALUE));
		treeA0 = findTreeItem(treeA, 2, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA0AA = findTreeItem(treeA0, 0, "aa");
		assertThat(treeA0AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 55, FieldChangeType.NONE, 11, null, 1, Integer.MAX_VALUE));
		treeA0AB = findTreeItem(treeA0, 1, "ab");
		assertThat(treeA0AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.REMOVED, null, null, 1, Integer.MAX_VALUE));
		treeA0AB0 = findTreeItem(treeA0AB, 1, "0");
		assertThat(treeA0AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.REMOVED, null, null, 1, Integer.MAX_VALUE));
		treeA0AB0ABA = findTreeItem(treeA0AB0, 0, "aba");
		assertThat(treeA0AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), null, FieldChangeType.REMOVED, 22, null, 1, Integer.MAX_VALUE));
		treeA1 = findTreeItem(treeA, 2, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 33, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		treeA1AB = findTreeItem(treeA1, 1, "ab");
		assertThat(treeA1AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		treeA1AB0 = findTreeItem(treeA1AB, 1, "0");
		assertThat(treeA1AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		treeA1AB0ABA = findTreeItem(treeA1AB0, 0, "aba");
		assertThat(treeA1AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), 66, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		
		// tree node at frame 4 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 4, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 3, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 3, Integer.MAX_VALUE));
		treeA0 = findTreeItem(treeA, 1, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA0AB = findTreeItem(treeA0, 1, "ab");
		assertThat(treeA0AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.REMOVED, null, null, 1, Integer.MAX_VALUE));
		treeA0AB0 = findTreeItem(treeA0AB, 1, "0");
		assertThat(treeA0AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.REMOVED, null, null, 1, Integer.MAX_VALUE));
		treeA0AB0ABA = findTreeItem(treeA0AB0, 0, "aba");
		assertThat(treeA0AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), null, FieldChangeType.REMOVED, 22, null, 1, Integer.MAX_VALUE));
		treeA1 = findTreeItem(treeA, 2, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		treeA1AA = findTreeItem(treeA1, 0, "aa");
		assertThat(treeA1AA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aa"), 33, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		treeA1AB = findTreeItem(treeA1, 1, "ab");
		assertThat(treeA1AB.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("ab"), null, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		treeA1AB0 = findTreeItem(treeA1AB, 1, "0");
		assertThat(treeA1AB0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), null, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
		treeA1AB0ABA = findTreeItem(treeA1AB0, 0, "aba");
		assertThat(treeA1AB0ABA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("aba"), 66, FieldChangeType.ADDED, null, null, 3, Integer.MAX_VALUE));
	}
	
	@Test
	public void testDynamicArrayElementRemovedForMultipleFrames() {
		var vmap = new X2VersionedMap(1);
		var visitor = new VersionedObjectVisitor(new PrimitiveInterner());
		
		visitor.setRootObject(1, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(2);
		visitor.visitIntValue(11);
		visitor.visitIntValue(22);
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		visitor.setRootObject(2, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitIntValue(11);
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		visitor.setRootObject(3, vmap);
		visitor.visitStructStart(null);
		visitor.visitProperty(new UnrealName("a"), 0);
		visitor.visitDynamicArrayStart(1);
		visitor.visitIntValue(33);
		visitor.visitDynamicArrayEnd();
		visitor.visitStructEnd();
		
		assertThat(vmap.getValueAt(1)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(11, 22)));
		assertThat(vmap.getValueAt(2)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(11)));
		assertThat(vmap.getValueAt(3)).usingRecursiveComparison().isEqualTo(Map.of("a", List.of(33)));
		
		// tree node at frame 1
		var treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 1, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		var treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 11, FieldChangeType.ADDED, null, 33, Integer.MIN_VALUE, 3));
		var treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), 22, FieldChangeType.ADDED, null, null, Integer.MIN_VALUE, 2));
		
		// tree node at frame 2 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA = findTreeItem(treeRoot, 2, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 11, FieldChangeType.NONE, null, 33, Integer.MIN_VALUE, 3));
		treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.REMOVED, 22, null, 1, Integer.MAX_VALUE));
		
		// tree node at frame 2 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 2, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 1, 3));
		treeA1 = findTreeItem(treeA, 0, "1");
		assertThat(treeA1.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("1"), null, FieldChangeType.REMOVED, 22, null, 1, Integer.MAX_VALUE));
		
		// tree node at frame 3 with onlyModified = false
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 3, false);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 33, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
		
		// tree node at frame 3 with onlyModified = true
		treeRoot = vmap.getTreeNodeAt(new PrimitiveInterner(), new UnrealName("test"), 3, true);
		assertThat(treeRoot.getChildren()).hasSize(1);
		assertThat(treeRoot.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("test"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA = findTreeItem(treeRoot, 1, "a");
		assertThat(treeA.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("a"), null, FieldChangeType.CHANGED, null, null, 2, Integer.MAX_VALUE));
		treeA0 = findTreeItem(treeA, 0, "0");
		assertThat(treeA0.getValue()).usingRecursiveComparison().isEqualTo(new X2VersionedDatumTreeItem(
				new UnrealName("0"), 33, FieldChangeType.CHANGED, 11, null, 1, Integer.MAX_VALUE));
	}
	
	private TreeItem<X2VersionedDatumTreeItem> findTreeItem(TreeItem<X2VersionedDatumTreeItem> node, int expectedChildren, String key) {
		var child = node.getChildren().stream().filter(c -> c.getValue().getName().getNormalized().equals(key)).findAny().orElse(null);
		assertThat(child).isNotNull();
		assertThat(child.getChildren()).hasSize(expectedChildren);
		return child;
	}
	
}
