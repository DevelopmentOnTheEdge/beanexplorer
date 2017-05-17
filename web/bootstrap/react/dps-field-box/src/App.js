import React, { Component } from 'react';
import DynamicProperty from './DynamicProperty';
import logo from './logo.svg';
import './App.css';

class App extends Component {

  constructor(props) {
    super(props);
    var testJson = require('./testJson.json');

    this.state = {
      fields: testJson
    };

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleJsonChange = this.handleJsonChange.bind(this);
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
        <h2>React component for DynamicPropertySet</h2>
      </div>
      <div className="App-intro">
        <div className="container">
          <div className="row">
            <div className="col-md-7">
              <form onSubmit={this.handleSubmit} className="bs-example">
                {this._createFields()}
                <div className="text-center">
                  <button type="submit" className="btn btn-primary btn-primary-spacing">Submit</button>
                  <button type="button" className="btn btn-default btn-primary-spacing">Cancel</button>
                </div>
              </form>
            </div>
            <div className="col-md-5">
              <textarea rows="20" name="inputJson" className="form-control" defaultValue={JSON.stringify(this.state.fields, null, 4)}
                        onChange={this.handleJsonChange} />
            </div>
          </div>
        </div>
      </div>
    </div>
    );
  }

  handleJsonChange(event){
    this.setState({fields: JSON.parse(event.target.value)});
  }

}

export default App;
