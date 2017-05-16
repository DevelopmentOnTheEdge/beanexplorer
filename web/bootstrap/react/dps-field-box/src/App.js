import React, { Component } from 'react';
import DynamicProperty from './DynamicProperty';
import logo from './logo.svg';
import './App.css';

class App extends Component {

  constructor(props) {
    super(props);
    this.state = {value: ''};
    this.state = {fields: [
      {
        "name":"previousValue",
        "title":"Предыдущие показания",
        "isReadOnly":true,
        "canBeNull":false,
        "reloadOnChange":false,
        "autoRefresh":false,
        "type":"textInput",
        "value":"52.000",
        "options":[
        ],
        "tips":{
        }
      },
      {
        "name":"currentConsumption",
        "title":"Текущий расход",
        "isReadOnly":false,
        "canBeNull":false,
        "reloadOnChange":true,
        "autoRefresh":false,
        "type":"textInput",
        "value":"0.000",
        "options":[
        ],
        "tips":{
        }
      }
    ]};

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    this.setState({value: event.target.value});
  }

  handleSubmit(event) {
    alert('A name was submitted: ' + this.state.value);
    event.preventDefault();
  }

  _onFieldChange(name, value) {
    const field = this.state.fields.find(field => field.name === name);
    field.value = value;

    // implicit state change => forceUpdate
//    this.forceUpdate(() => {
//      this.setState({ allFieldsFilled: this._allFieldsFilled() });
//
//      if (field.reloadOnChange || field.autoRefresh) {
//        this._reloadOnChange(name);
//      }
//    });
  }

  _createFields() {
    let curGroup = [];
    let curGroupName = null, curGroupId = null;
    let result = [];

    const finishGroup = () => {
      if(curGroup.length > 0) {
        if(curGroupId) {
          result.push(this._createGroup(curGroup, curGroupId, curGroupName));
        } else {
          Array.prototype.push.apply(result, curGroup);
        }
      }
      curGroup = [];
    };

    for(const json of this.state.fields) {
      const newGroup = json.group;
      const newGroupName = newGroup ? newGroup.name : null;
      const newGroupId = newGroup ? newGroup.id : null;
      if(newGroupId !== curGroupId) {
        finishGroup();
        curGroupName = newGroupName;
        curGroupId = newGroupId;
      }
      const field = (<DynamicProperty value={json} ref={json.name} key={json.name} onChange={this._onFieldChange}/>);
      curGroup.push(field);
    }
    finishGroup();
    return result;
  }

  render() {
    return (
    <div className="App">
      <div className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <h2>Welcome to React</h2>
      </div>
      <div className="App-intro">
        <form onSubmit={this.handleSubmit}>
          {this._createFields()}
          <input type="submit" value="Submit" />
        </form>
      </div>
    </div>
    );
  }

}

export default App;
