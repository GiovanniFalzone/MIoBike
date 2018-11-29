/**
 * @module ol/format/filter/Or
 */
import LogicalNary from '../filter/LogicalNary.js';

/**
 * @classdesc
 * Represents a logical `<Or>` operator between two ore more filter conditions.
 * @api
 */
class Or extends LogicalNary {

  /**
   * @param {...import("./Filter.js").default} conditions Conditions.
   */
  constructor(conditions) {
    super('Or', Array.prototype.slice.call(arguments));
  }

}

export default Or;
