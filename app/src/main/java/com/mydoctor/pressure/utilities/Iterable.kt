package com.mydoctor.pressure.utilities

/**
 * Returns a triple of lists, where
 * - *first* list is built from the first values of each pair from this collection,
 * - *second* list is built from the second values of each pair from this collection.
 * - *third* list is built from the third values of each pair from this collection.
 */
fun <A, B, C> List<Triple<A, B, C>>.unzip(): Triple<List<A>, List<B>, List<C>> {
    val expectedSize = this.size
    val listA = ArrayList<A>(expectedSize)
    val listB = ArrayList<B>(expectedSize)
    val listC = ArrayList<C>(expectedSize)
    for (triple in this) {
        listA.add(triple.first)
        listB.add(triple.second)
        listC.add(triple.third)
    }
    return Triple(listA, listB, listC)
}

/**
 * Returns a list of values `zipped by Triple` from the elements of three lists
 * The returned list has length of the first list.
 */
fun <A, B, C> zip(aList: List<A>, bList: List<B>, cList: List<C>): List<Triple<A, B, C>> {
    val expectedSize = aList.size
    return buildList(expectedSize) {
        repeat(expectedSize) {
            add(Triple(aList[it], bList[it], cList[it]))
        }
    }
}