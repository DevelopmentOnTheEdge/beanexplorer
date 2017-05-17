import React, { Component } from 'react';
import DynamicProperty from './DynamicProperty';
import JsonPointer from 'json-pointer';

class DynamicPropertySet extends Component {

  render() {
    let curGroup = [];
    let curGroupName = null, curGroupId = null;
    let fields = [];

    const finishGroup = () => {
      if(curGroup.length > 0) {
        if(curGroupId) {
          fields.push(this._createGroup(curGroup, curGroupId, curGroupName));
        } else {
          Array.prototype.push.apply(fields, curGroup);
        }
      }
      curGroup = [];
    };

    for(const item of this.props.fields.order) {
      var itemName = item.substring(item.lastIndexOf("/")+1);
      var itemMeta = JsonPointer.get(this.props.fields, "/meta" + item);
      var itemValue = JsonPointer.get(this.props.fields, "/values" + item);

      const newGroup = itemMeta.group;
      const newGroupName = newGroup ? newGroup.name : null;
      const newGroupId = newGroup ? newGroup.id : null;
      if(newGroupId !== curGroupId) {
        finishGroup();
        curGroupName = newGroupName;
        curGroupId = newGroupId;
      }
      const field = (<DynamicProperty meta={itemMeta} name={itemName} value={itemValue} path={item}
                                      key={itemName} ref={itemName} onChange={this.props.onChange}/>);
      curGroup.push(field);
    }
    finishGroup();

    return (
      <div className="dynamic-property-set">
        {fields}
      </div>
    );
  }

}

export default DynamicPropertySet;
