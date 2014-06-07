package scalatags
package generic
import acyclic.file

/**
 * An abstract representation of the Scalatags package. This allows you to
 * customize Scalatags to work with different backends, by defining your own
 * implementation of [[Bundle[T].Tag]], and specifying how the various [[Attr]]s
 * and [[Style]]s contribute to construct the [[T]]. Apart from satisfying the
 * default String/Boolean/Numeric implementations of [[Attr]] and [[Style]],
 * you can also define your own, e.g. ScalaJS ships with an implicit conversion
 * from `js.Any` to `Attr`, so that you can attach objects to the resultant
 * `dom.Element` without serializing them.
 *
 * By default, Scalatags ships with [[scalatags.Text]]: `Bundle[StringBuilder]`
 * on all platforms, and [[scalatags.JsDom]]: `Bundle[dom.Element]` on ScalaJS.
 *
 * It is possible to write entirely backend-agnostic Scalatags code by making
 * your code parametric on a `Bundle[T]` (or some subclass of it), and importing
 * from that rather than importing directly from [[scalatags.JsDom]] or
 * [[scalatags.Text]]. You will naturally only be able to use functionality
 * (e.g. implicit conversions to [[Attr]]s and [[Style]]s which are present
 * in the common interface.
 *
 * @tparam T The type to which [[Attr]]s and [[Style]]s are applied to when the
 *           `Tag` is being rendered to give a final result.
 */
trait Bundle[T]{
  /**
   * Convenience object for importing all of Scalatags' functionality at once
   */
  val all: Attrs with Styles with Tags with DataConverters with Util
  /**
   * Convenience object for importing only Scalatags' tags (e.g. `div`, `p`)
   * into the local namespace, while leaving Styles and Attributes accessible
   * via the `*` object
   */
  val short: AbstractShort[T]
  val attrs: Attrs
  val tags: Tags
  val tags2: Tags2
  val styles: Styles
  val styles2: Styles2
  val svgTags: SvgTags
  val svgStyles: SvgStyles

  type Attrs = generic.Attrs[T]
  type Tags = generic.Tags[T]
  type Tags2 = generic.Tags2[T]
  type Styles = generic.Styles[T]
  type Styles2 = generic.Styles2[T]
  type SvgTags = generic.SvgTags[T]
  type SvgStyles = generic.SvgStyles[T]
  type Util = generic.Util[T]

  type Attr = generic.Attr
  type Style = generic.Style
  type Modifier = generic.Modifier[T]
  type AttrValue[V] = generic.AttrValue[T, V]
  type StyleValue[V] = generic.StyleValue[T, V]

  implicit def stringAttr: AttrValue[String]
  implicit def booleanAttr: AttrValue[Boolean]
  implicit def numericAttr[V: Numeric]: AttrValue[V]
  implicit def stringStyle: StyleValue[String]
  implicit def booleanStyle: StyleValue[Boolean]
  implicit def numericStyle[V: Numeric]: StyleValue[V]

  /**
   * Allows you to modify a HtmlTag by adding a String to its list of children
   */
  implicit def stringNode(v: String): Modifier
  /**
   * Lets you put numbers into a scalatags tree, converting them to Strings
   */
  implicit def NumericModifier[V: Numeric](u: V): Modifier

  type Tag <: generic.TypedTag[Platform.Base, T]

  /**
   * A [[Node]] which contains a String which will not be escaped.
   */
  type RawNode <: Modifier
  val RawNode: Companion[RawNode]

  /**
   * Delimits a string that should be included in the result as raw,
   * un-escaped HTML
   */
  def raw(s: String): RawNode
  /**
   * A [[Node]] which contains a String.
   */
  type StringNode <: Modifier
  val StringNode: Companion[StringNode]
}

trait AbstractShort[T]{
  val `*`: generic.Attrs[T] with generic.Styles[T]
}