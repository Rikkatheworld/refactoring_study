fun <A> flattenResult(list: MyList<Result<A>>): MyList<A> =
    list.flatmap { ra: Result<A> -> ra.map { MyList(it) }.getOrElse { MyList() } }

fun <A, B, C> lift2(f: (A) -> (B) -> C): (Result<A>) -> (Result<B>) -> Result<C> = { a ->
    { b ->
        a.map(f).flatMap { b.map(it) }
    }
}

fun <A, B, C> map2(a: Result<A>, b: Result<B>, f: (A) -> (B) -> C): Result<C> = lift2(f)(a)(b)

//fun <A> sequence(list: MyList<Result<A>>): Result<MyList<A>> =
//    list.foldRight(Result(MyList())) { x ->
//        { y ->
//            map2(x, y) { a ->
//                { b ->
//                    b.cons(a)
//                }
//            }
//        }
//    }

fun <A, B> traverse(list: MyList<A>, f: (A) -> Result<B>): Result<MyList<B>> =
    list.foldRight(Result(MyList())) { x ->
        { y: Result<MyList<B>> ->
            map2(f(x), y) { a ->
                { b: MyList<B> ->
                    b.cons(a)
                }
            }
        }
    }

fun <A> sequence(list: MyList<Result<A>>): Result<MyList<A>> =
    traverse(list) { it }