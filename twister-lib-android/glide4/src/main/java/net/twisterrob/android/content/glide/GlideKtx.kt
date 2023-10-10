@file:Suppress("NOTHING_TO_INLINE") // `inline` hides methods from Java.

package net.twisterrob.android.content.glide

import com.bumptech.glide.Registry
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import kotlin.reflect.KClass

@Deprecated(
	"Not recommended, registered classes are hidden. Use 'replace with' to infer types.",
	ReplaceWith("this.append(Data::class, TResource::class, decoder)"),
	level = DeprecationLevel.ERROR,
)
inline fun <reified Data, reified TResource> Registry.append(
	decoder: ResourceDecoder<Data, TResource>
): Registry =
	this.append(Data::class.java, TResource::class.java, decoder)

inline fun <Data : Any, TResource : Any> Registry.append(
	dataClass: KClass<Data>,
	resourceClass: KClass<TResource>,
	decoder: ResourceDecoder<Data, TResource>
): Registry =
	this.append(dataClass.java, resourceClass.java, decoder)

@Deprecated(
	"Not recommended, registered classes are hidden. Use 'replace with' to infer types.",
	ReplaceWith("this.register(TResource::class, Transcode::class, transcoder)"),
	level = DeprecationLevel.ERROR,
)
inline fun <reified TResource, reified Transcode> Registry.register(
	transcoder: ResourceTranscoder<TResource, Transcode>
): Registry =
	this.register(TResource::class.java, Transcode::class.java, transcoder)

inline fun <TResource : Any, Transcode : Any> Registry.register(
	resourceClass: KClass<TResource>,
	transcodeClass: KClass<Transcode>,
	transcoder: ResourceTranscoder<TResource, Transcode>,
): Registry =
	this.register(resourceClass.java, transcodeClass.java, transcoder)
