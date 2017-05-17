import React, { Component } from 'react';
import DynamicProperty from './DynamicProperty';

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

    for(const json of this.props.fields) {
      const newGroup = json.group;
      const newGroupName = newGroup ? newGroup.name : null;
      const newGroupId = newGroup ? newGroup.id : null;
      if(newGroupId !== curGroupId) {
        finishGroup();
        curGroupName = newGroupName;
        curGroupId = newGroupId;
      }
      const field = (<DynamicProperty value={json} ref={json.name} key={json.name} onChange={this.props.onChange}/>);
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