package scalatags
package generic

import scalatags.Platform._
import scalatags._

/**
 * Created by haoyi on 6/2/14.
 */
trait Util[Target] {
  def makeAbstractTypedHtmlTag[T <: Base](tag: String, void: Boolean): AbstractTypedHtmlTag[T, Target]
  implicit def stringAttr(s: String): AttrVal[Target]
  implicit def stringStyle(s: String): StyleVal[Target]

  /**
   * Provides extension methods on strings to fit them into Scalatag fragments.
   */
  implicit class ExtendedString(s: String){
    /**
     * Converts the string to a [[HtmlTag]]
     */
    def tag[T <: Base] = {
      if (!Escaping.validTag(s))
        throw new IllegalArgumentException(
          s"Illegal tag name: $s is not a valid XML tag name"
        )
      makeAbstractTypedHtmlTag[T](s, false)
    }
    /**
     * Converts the string to a void [[HtmlTag]]; that means that they cannot
     * contain any content, and can be rendered as self-closing tags.
     */
    def voidTag[T <: Base] = {
      if (!Escaping.validTag(s))
        throw new IllegalArgumentException(
          s"Illegal tag name: $s is not a valid XML tag name"
        )
      makeAbstractTypedHtmlTag[T](s, true)
    }
    /**
     * Converts the string to a [[UntypedAttr]]
     */
    def attr = {
      if (!Escaping.validAttrName(s))
        throw new IllegalArgumentException(
          s"Illegal attribute name: $s is not a valid XML attribute name"
        )
      Attr(s)
    }
    /**
     * Converts the string to a [[TypedAttr]]
     */
    def attrTyped[T] = {
      if (!Escaping.validAttrName(s))
        throw new IllegalArgumentException(
          s"Illegal attribute name: $s is not a valid XML attribute name"
        )
      Attr(s)
    }
    /**
     * Converts the string to a [[Style]]. The string is used as the cssName of the
     * style, and the jsName of the style is generated by converted the dashes
     * to camelcase.
     */
    def style = Style(camelCase(s), s)

  }

  implicit object styleOrdering extends Ordering[Style]{
    override def compare(x: Style, y: Style): Int = x.cssName compareTo y.cssName
  }

  implicit object attrOrdering extends Ordering[Attr]{
    override def compare(x: Attr, y: Attr): Int = x.name compareTo y.name
  }
  /**
   * Allows you to modify a [[HtmlTag]] by adding a Seq containing other nest-able
   * objects to its list of children.
   */
  implicit class SeqModifier[A <% Modifier[Target]](xs: Seq[A]) extends Modifier[Target]{
    def transforms = xs.flatMap(_.transforms).toArray
  }

  /**
   * Allows you to modify a [[HtmlTag]] by adding an Option containing other nest-able
   * objects to its list of children.
   */
  implicit def OptionModifier[A <% Modifier[Target]](xs: Option[A]) = new SeqModifier(xs.toSeq)

  /**
   * Allows you to modify a [[HtmlTag]] by adding an Array containing other nest-able
   * objects to its list of children.
   */
  implicit def ArrayModifier[A <% Modifier[Target]](xs: Array[A]) = new SeqModifier[A](xs.toSeq)

  /**
   * Lets you put Unit into a scalatags tree, as a no-op.
   */
  implicit def UnitModifier(u: Unit) = new Modifier[Target]{
    def transforms = Array.empty
  }
}